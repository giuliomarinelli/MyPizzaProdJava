package backendapp.myPizza.SocketIO.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class SessionTrackingService {

    private final Set<SessionManager> sessionTracker = new HashSet<>();

    public boolean isOnLine(UUID userId) {
        return sessionTracker.stream().anyMatch(sm -> sm.getUserId().equals(userId));
    }

    public void addSession(UUID userId, UUID sessionId) {
        Optional<SessionManager> userSession =
                sessionTracker.stream().filter(sm -> sm.getUserId().equals(userId)).findFirst();
        if (userSession.isPresent()) {
            userSession.get().getSessionIds().add(sessionId);
        } else {
            SessionManager sessionManager = new SessionManager();
            sessionManager.setUserId(userId);
            sessionManager.getSessionIds().add(sessionId);
            sessionTracker.add(sessionManager);
        }
    }

    public UUID getUserIdFromSessionId(UUID sessionId) {
        Optional<SessionManager> opt = sessionTracker.stream().filter(sm -> sm.getSessionIds().contains(sessionId))
                .findFirst();
        return opt.map(SessionManager::getUserId).orElse(null);
    }

    public Set<UUID> getClientIdsFromUserId(UUID userId) {
        return sessionTracker.stream().filter(sm -> sm.getUserId().equals(userId))
                .map(SessionManager::getSessionIds).findFirst().orElse(null);
    }

    public void removeSession(UUID sessionId) {
        Optional<SessionManager> opt = sessionTracker.stream()
                .filter(sm -> sm.getSessionIds().contains(sessionId)).findFirst();
        if (opt.isPresent()) {
            SessionManager sessionManager = opt.get();
            sessionManager.getSessionIds().remove(sessionId);
            sessionTracker.removeIf(sm -> sm.getSessionIds().isEmpty());
        }
    }

}

