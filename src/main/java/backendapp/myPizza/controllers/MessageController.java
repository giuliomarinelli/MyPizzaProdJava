package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.SocketIO.entities.Message;
import backendapp.myPizza.SocketIO.services.MessageService;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageSvc;


    @GetMapping("/{id}")
    public Message getMessageById(@PathVariable UUID id) throws UnauthorizedException, NotFoundException {
        return messageSvc.getReceivedMessageByIdForUser(id);
    }


    @GetMapping("/{id}/set-unread")
    public Message setUnread(@PathVariable UUID id) throws UnauthorizedException, NotFoundException {
        return messageSvc.setMessageAsReadById(id);
    }




}
