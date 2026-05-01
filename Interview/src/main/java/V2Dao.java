import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V2Dao {

    private V2Dao() {
    }

    public static int ensureTestForDate(int dateValue, String name, int durationSeconds) {
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement find = con.prepareStatement("select id from tests where scheduled_date=?")) {
                find.setInt(1, dateValue);
                ResultSet rs = find.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            try (PreparedStatement insert = con.prepareStatement(
                    "insert into tests(name,scheduled_date,duration_seconds,status) values(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, name == null || name.trim().isEmpty() ? "Test " + dateValue : name.trim());
                insert.setInt(2, dateValue);
                insert.setInt(3, Math.max(60, durationSeconds));
                insert.setString(4, "active");
                insert.executeUpdate();
                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure test", e);
        }
        return -1;
    }

    public static int createOrGetCandidateAttempt(int userId, int testId) {
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement find = con.prepareStatement("select id from candidate_test where user_id=? and test_id=?")) {
                find.setInt(1, userId);
                find.setInt(2, testId);
                ResultSet rs = find.executeQuery();
                if (rs.next()) {
                    int attemptId = rs.getInt("id");
                    try (PreparedStatement reset = con.prepareStatement(
                            "update candidate_test set status='blocked', started_at=null, ends_at=null, submitted_at=null, score=0, total_questions=0, extra_seconds=0 where id=?")) {
                        reset.setInt(1, attemptId);
                        reset.executeUpdate();
                    }
                    return attemptId;
                }
            }

            try (PreparedStatement insert = con.prepareStatement(
                    "insert into candidate_test(user_id,test_id,status) values(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                insert.setInt(1, userId);
                insert.setInt(2, testId);
                insert.setString(3, "blocked");
                insert.executeUpdate();
                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create candidate attempt", e);
        }
        return -1;
    }

    public static Map<String, Object> getAttemptById(int attemptId) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "select ct.id,ct.user_id,ct.test_id,ct.status,ct.started_at,ct.ends_at,ct.submitted_at,ct.extra_seconds," +
                             "t.name as test_name,t.duration_seconds,t.scheduled_date " +
                             "from candidate_test ct join tests t on ct.test_id=t.id where ct.id=?")) {
            ps.setInt(1, attemptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", rs.getInt("id"));
                map.put("user_id", rs.getInt("user_id"));
                map.put("test_id", rs.getInt("test_id"));
                map.put("status", rs.getString("status"));
                map.put("started_at", rs.getTimestamp("started_at"));
                map.put("ends_at", rs.getTimestamp("ends_at"));
                map.put("submitted_at", rs.getTimestamp("submitted_at"));
                map.put("extra_seconds", rs.getInt("extra_seconds"));
                map.put("duration_seconds", rs.getInt("duration_seconds"));
                map.put("test_name", rs.getString("test_name"));
                map.put("scheduled_date", rs.getInt("scheduled_date"));
                return map;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load attempt", e);
        }
        return null;
    }

    public static Map<String, Object> getAttemptForUser(int attemptId, int userId) {
        Map<String, Object> attempt = getAttemptById(attemptId);
        if (attempt == null) {
            return null;
        }
        if (((Integer) attempt.get("user_id")).intValue() != userId) {
            return null;
        }
        return attempt;
    }

    public static int getLatestAttemptForUser(int userId) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "select id from candidate_test where user_id=? order by id desc limit 1")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load latest attempt", e);
        }
        return -1;
    }

    public static Map<String, Object> startAttemptIfAllowed(int attemptId) {
        Map<String, Object> attempt = getAttemptById(attemptId);
        if (attempt == null) {
            return null;
        }
        String status = String.valueOf(attempt.get("status"));
        if (!"allowed".equalsIgnoreCase(status) && !"in_progress".equalsIgnoreCase(status)) {
            return attempt;
        }

        Timestamp startedAt = (Timestamp) attempt.get("started_at");
        Timestamp endsAt = (Timestamp) attempt.get("ends_at");
        int duration = ((Integer) attempt.get("duration_seconds")).intValue();
        int extra = ((Integer) attempt.get("extra_seconds")).intValue();
        int totalSeconds = Math.max(60, duration + extra);
        long nowMillis = System.currentTimeMillis();
        long maxExpectedEndMillis = nowMillis + (long) totalSeconds * 1000L + 120000L;

        try (Connection con = DBUtil.getConnection()) {
            if (startedAt == null || endsAt == null) {
                try (PreparedStatement update = con.prepareStatement(
                        "update candidate_test set status='in_progress', started_at=now(), ends_at=date_add(now(), interval ? second) where id=?")) {
                    update.setInt(1, totalSeconds);
                    update.setInt(2, attemptId);
                    update.executeUpdate();
                }
            } else if (endsAt.getTime() > maxExpectedEndMillis) {
                try (PreparedStatement repair = con.prepareStatement(
                        "update candidate_test set status='in_progress', started_at=now(), ends_at=date_add(now(), interval ? second) where id=?")) {
                    repair.setInt(1, totalSeconds);
                    repair.setInt(2, attemptId);
                    repair.executeUpdate();
                }
            } else if ("allowed".equalsIgnoreCase(status)) {
                try (PreparedStatement update = con.prepareStatement("update candidate_test set status='in_progress' where id=?")) {
                    update.setInt(1, attemptId);
                    update.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start attempt", e);
        }
        return getAttemptById(attemptId);
    }

    public static List<Map<String, Object>> getQuestionsForTest(int testId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "select id,question_text,opt1,opt2,answer,marks from test_questions where test_id=? order by sort_order asc, id asc")) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", rs.getInt("id"));
                map.put("question_text", rs.getString("question_text"));
                map.put("opt1", rs.getString("opt1"));
                map.put("opt2", rs.getString("opt2"));
                map.put("answer", rs.getString("answer"));
                map.put("marks", rs.getInt("marks"));
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load questions", e);
        }
        return list;
    }

    public static void saveAnswer(int attemptId, int questionId, String selectedOption) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "insert into candidate_answers(candidate_test_id,question_id,selected_option) values(?,?,?) " +
                             "on duplicate key update selected_option=values(selected_option), updated_at=current_timestamp")) {
            ps.setInt(1, attemptId);
            ps.setInt(2, questionId);
            ps.setString(3, selectedOption);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save answer", e);
        }
    }

    public static Map<String, Integer> submitAttempt(int attemptId, Map<Integer, String> answers) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        int correct = 0;
        int wrong = 0;
        int total = 0;

        Map<String, Object> attempt = getAttemptById(attemptId);
        if (attempt == null) {
            result.put("correct", 0);
            result.put("wrong", 0);
            result.put("score", 0);
            result.put("total", 0);
            return result;
        }

        String status = String.valueOf(attempt.get("status"));
        Timestamp endsAt = (Timestamp) attempt.get("ends_at");
        boolean timedOut = endsAt != null && endsAt.before(new Timestamp(System.currentTimeMillis()));
        if (timedOut) {
            status = "timed_out";
        }

        List<Map<String, Object>> questions = getQuestionsForTest(((Integer) attempt.get("test_id")).intValue());
        for (Map<String, Object> q : questions) {
            total++;
            int questionId = ((Integer) q.get("id")).intValue();
            String expected = String.valueOf(q.get("answer"));
            String selected = answers.get(Integer.valueOf(questionId));
            if (selected != null) {
                saveAnswer(attemptId, questionId, selected);
            }
            if (selected != null && selected.equals(expected)) {
                correct++;
            } else {
                wrong++;
            }
        }

        int score = total == 0 ? 0 : (correct * 100) / total;
        String finalStatus = "submitted";
        if ("blocked".equalsIgnoreCase(status)) {
            finalStatus = "blocked";
        } else if (timedOut) {
            finalStatus = "timed_out";
        }

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "update candidate_test set status=?, submitted_at=now(), score=?, total_questions=? where id=?")) {
            ps.setString(1, finalStatus);
            ps.setInt(2, score);
            ps.setInt(3, total);
            ps.setInt(4, attemptId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit attempt", e);
        }

        result.put("correct", correct);
        result.put("wrong", wrong);
        result.put("score", score);
        result.put("total", total);
        return result;
    }

    public static List<Map<String, Object>> getAllTests() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "select id,name,scheduled_date,duration_seconds,status,created_at from tests order by scheduled_date desc, id desc")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", rs.getInt("id"));
                map.put("name", rs.getString("name"));
                map.put("scheduled_date", rs.getInt("scheduled_date"));
                map.put("duration_seconds", rs.getInt("duration_seconds"));
                map.put("status", rs.getString("status"));
                map.put("created_at", rs.getTimestamp("created_at"));
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tests", e);
        }
        return list;
    }

    public static int createTest(String name, int scheduledDate, int durationSeconds) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "insert into tests(name,scheduled_date,duration_seconds,status) values(?,?,?,?)")) {
            ps.setString(1, name);
            ps.setInt(2, scheduledDate);
            ps.setInt(3, Math.max(60, durationSeconds));
            ps.setString(4, "active");
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test", e);
        }
    }

    public static int updateTestDuration(int testId, int durationSeconds) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("update tests set duration_seconds=? where id=?")) {
            ps.setInt(1, Math.max(60, durationSeconds));
            ps.setInt(2, testId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update duration", e);
        }
    }

    public static int addQuestion(int testId, String question, String opt1, String opt2, String answer) {
        try (Connection con = DBUtil.getConnection()) {
            int nextSort = 1;
            try (PreparedStatement countPs = con.prepareStatement("select coalesce(max(sort_order),0)+1 as next_order from test_questions where test_id=?")) {
                countPs.setInt(1, testId);
                ResultSet rs = countPs.executeQuery();
                if (rs.next()) {
                    nextSort = rs.getInt("next_order");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "insert into test_questions(test_id,question_text,opt1,opt2,answer,marks,sort_order) values(?,?,?,?,?,?,?)")) {
                ps.setInt(1, testId);
                ps.setString(2, question);
                ps.setString(3, opt1);
                ps.setString(4, opt2);
                ps.setString(5, answer);
                ps.setInt(6, 1);
                ps.setInt(7, nextSort);
                return ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add question", e);
        }
    }

    public static List<Map<String, Object>> getQuestionsByTest(int testId) {
        return getQuestionsForTest(testId);
    }

    public static List<Map<String, Object>> getCandidatesByTest(int testId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "select ct.id as attempt_id, u.id as user_id, u.name, u.email, u.city, u.date, ct.status, ct.started_at, ct.ends_at, ct.extra_seconds " +
                             "from candidate_test ct join users u on ct.user_id=u.id where ct.test_id=? order by ct.id desc")) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("attempt_id", rs.getInt("attempt_id"));
                map.put("user_id", rs.getInt("user_id"));
                map.put("name", rs.getString("name"));
                map.put("email", rs.getString("email"));
                map.put("city", rs.getString("city"));
                map.put("date", rs.getString("date"));
                map.put("status", rs.getString("status"));
                map.put("started_at", rs.getTimestamp("started_at"));
                map.put("ends_at", rs.getTimestamp("ends_at"));
                map.put("extra_seconds", rs.getInt("extra_seconds"));
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load candidates", e);
        }
        return list;
    }

    public static int updateCandidateStatus(int attemptId, String status) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("update candidate_test set status=? where id=?")) {
            ps.setString(1, status);
            ps.setInt(2, attemptId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update candidate status", e);
        }
    }

    public static int addExtraTime(int attemptId, int extraSeconds) {
        int bounded = Math.max(10, extraSeconds);
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "update candidate_test set extra_seconds=extra_seconds+?, ends_at=case when ends_at is null then null else date_add(ends_at, interval ? second) end where id=?")) {
                ps.setInt(1, bounded);
                ps.setInt(2, bounded);
                ps.setInt(3, attemptId);
                return ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add extra time", e);
        }
    }
}
