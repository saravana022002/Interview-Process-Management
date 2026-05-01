import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/candidate/{attemptId}")
public class CandidateWebSocket {

    private static final ConcurrentHashMap<Integer, Set<Session>> ATTEMPT_SESSIONS = new ConcurrentHashMap<Integer, Set<Session>>();

    @OnOpen
    public void onOpen(Session session, @PathParam("attemptId") String attemptIdText) {
        try {
            int attemptId = Integer.parseInt(attemptIdText);
            ATTEMPT_SESSIONS.computeIfAbsent(Integer.valueOf(attemptId), k -> ConcurrentHashMap.newKeySet()).add(session);
            session.getBasicRemote().sendText("CONNECTED");
        } catch (Exception e) {
            try {
                session.close();
            } catch (IOException ex) {
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("attemptId") String attemptIdText) {
        try {
            int attemptId = Integer.parseInt(attemptIdText);
            Set<Session> sessions = ATTEMPT_SESSIONS.get(Integer.valueOf(attemptId));
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    ATTEMPT_SESSIONS.remove(Integer.valueOf(attemptId));
                }
            }
        } catch (Exception e) {
        }
    }

    public static void broadcastStatus(int attemptId, String event) {
        Set<Session> sessions = ATTEMPT_SESSIONS.get(Integer.valueOf(attemptId));
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        for (Session session : sessions) {
            if (session != null && session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(event);
                } catch (IOException e) {
                }
            }
        }
    }
}
