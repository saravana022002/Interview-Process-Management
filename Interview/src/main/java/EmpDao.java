import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class EmpDao {

    private EmpDao() {
    }

    public static Connection getConnection() {
        try {
            return DBUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to database", e);
        }
    }

    public static int saveAndReturnId(Emp e) {
        try (Connection con = EmpDao.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "insert into users(name,date,email,city,status_value) values (?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDate());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getCity());
            ps.setString(5, e.getStatus());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}
