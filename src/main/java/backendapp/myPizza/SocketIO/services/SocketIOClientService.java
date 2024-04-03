package backendapp.myPizza.SocketIO.services;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketIOClientService {

    private final Map<UUID, SocketIOClient> clients = new ConcurrentHashMap<>();

    public void addClient(UUID sessionId, SocketIOClient client) {
        clients.put(sessionId, client);
    }

    public SocketIOClient getClient(UUID sessionId) {
        return clients.get(sessionId);
    }

    public void removeClient(UUID sessionId) {
        clients.remove(sessionId);
    }



}