package backendapp.myPizza.SocketIO.services;

import backendapp.myPizza.Models.entities.Order;
import backendapp.myPizza.Models.entities.TimeInterval;
import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.entities.WorkSession;
import backendapp.myPizza.SocketIO.ClientNotification;
import backendapp.myPizza.SocketIO.entities.Message;
import backendapp.myPizza.SocketIO.repositories.MessageRepository;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.repositories.UserRepository;
import backendapp.myPizza.repositories.WorkSessionRepository;
import backendapp.myPizza.security.JwtUtils;
import backendapp.myPizza.services.ProfileService;
import backendapp.myPizza.services._SessionService;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
//@Log4j2
public class MessageService {

    @Autowired
    private SessionTrackingService sessionSvc;

    @Autowired
    private SocketIOClientService clientSvc;

    @Autowired
    private MessageRepository messageRp;

    @Autowired
    private ProfileService profileSvc;

    @Autowired
    private _SessionService _session;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRp;

    public Message getMessageById(UUID id) throws NotFoundException {
        return messageRp.findById(id).orElseThrow(
                () -> new NotFoundException("Message with id ='" + id + "' not found")
        );
    }

    public Message getReceivedMessageByIdForUser(UUID id) throws UnauthorizedException, NotFoundException {
        UUID userId = jwtUtils.extractUserIdFromReq();
        assert userRp.findById(userId).isPresent();
        User user = userRp.findById(userId).get();
        Message message = getMessageById(id);
        if (!message.getRecipientUser().equals(user))
            throw new UnauthorizedException("You don't have permissions to access this resource");
        return message;
    }

    public List<Message> getAllReceivedForUser() throws UnauthorizedException {
        UUID userId = jwtUtils.extractUserIdFromReq();
        assert userRp.findById(userId).isPresent();
        return messageRp.findAllMessagesByRecipientUserId(userId);
    }

    public List<Message> getAllReceivedUnreadForUser() throws UnauthorizedException {
        return getAllReceivedForUser().stream().filter(m -> !m.isRead()).toList();
    }


    public Message setMessageAsReadById(UUID id) throws NotFoundException, UnauthorizedException {
        Message m = getMessageById(id);
        UUID userId = jwtUtils.extractUserIdFromReq();
        if (!m.getRecipientUser().getId().equals(userId))
            throw new UnauthorizedException("You don't have permissions to access this resource");
        m.setRead(true);
        messageRp.save(m);
        return m;
    }

    public void sendMessageToClient(Message message) {


        Set<UUID> clientIds = sessionSvc.getClientIdsFromUserId(message.getRecipientUser().getId());
//        log.info(message.getRecipientUser().getId() + " receiving message");
        for (UUID clientId : clientIds) {
            SocketIOClient client = clientSvc.getClient(clientId);
//            log.info(client);
            if (client != null) {
//                log.info("send message");
                client.sendEvent("message", message);
            }

        }
    }

    public void restoreMessages(UUID recipientUserId) {
        if (sessionSvc.isOnLine(recipientUserId)) {
            List<Message> messages = messageRp.findAllUnreadMessagesByRecipientUserId(recipientUserId);
            messages = messages.parallelStream().peek(m -> {
                m.setRestore(true);
            }).toList();
            messageRp.saveAll(messages);
            for (Message m : messages) {
                sendMessageToClient(m);
            }
        }
    }

    public void getUnreadMessageCount(UUID userId) {
        Set<UUID> clientIds = sessionSvc.getClientIdsFromUserId(userId);
        Long count = messageRp.getUnreadMessagesCountByRecipientUserId(userId);
        for (UUID clientId : clientIds) {
            SocketIOClient client = clientSvc.getClient(clientId);
            if (client != null) {
                client.sendEvent("unread_messages_count", count);
            }
        }

    }

    public void setAllMessagesAsRestored(UUID userId) {
        List<Message> messages = messageRp.findAllUnreadMessagesByRecipientUserId(userId);
        messageRp.saveAll(messages.parallelStream().peek(m -> m.setRestore(true)).toList());
    }


public void restoreAllMessages(UUID recipientUserId) {
        if (sessionSvc.isOnLine(recipientUserId)) {
            List<Message> messages = messageRp.findAllMessagesByRecipientUserId(recipientUserId);
            messages = messages.parallelStream().peek(m -> {
                m.setRestore(true);
            }).toList();
            messageRp.saveAll(messages);
            for (Message m : messages) {
                sendMessageToClient(m);
            }
        }
    }



    public void pushTimeIntervals() throws NotFoundException {
        Set<UUID> adminClientIds = sessionSvc.getClientIdsFromUserId(profileSvc.getAdminUserId().getAdminUserId());
        List<TimeInterval> timeIntervals = _session.getActiveSessionTimeIntervals();
        System.out.println("push time interval");

        for (UUID clientId : adminClientIds) {
            SocketIOClient client = clientSvc.getClient(clientId);
//            log.info(client);
            if (client != null) {
//                log.info("send active session");
                client.sendEvent("time_intervals", timeIntervals);
            }

        }
    }


}
