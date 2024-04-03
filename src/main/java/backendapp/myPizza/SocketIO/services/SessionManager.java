package backendapp.myPizza.SocketIO.services;

import lombok.Data;

import java.util.*;

@Data
public class SessionManager {
    private UUID userId;
    private Set<UUID> sessionIds = new HashSet<>();
}
