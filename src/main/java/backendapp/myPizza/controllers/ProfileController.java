package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.Address;
import backendapp.myPizza.Models.entities.Order;
import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.reqDTO.ChangePasswordDTO;
import backendapp.myPizza.Models.reqDTO.UserPutDTO;
import backendapp.myPizza.Models.resDTO.AdminUserIdRes;
import backendapp.myPizza.Models.resDTO.AuthoritiesRes;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.Models.resDTO.IsLoggedInRes;
import backendapp.myPizza.SocketIO.entities.Message;
import backendapp.myPizza.SocketIO.services.MessageService;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.exceptions.Validation;
import backendapp.myPizza.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-profile")
public class ProfileController {

    @Autowired
    private ProfileService profileSvc;

    @Autowired
    private MessageService messageSvc;

    @GetMapping("")
    public User get() throws UnauthorizedException {
        return profileSvc.get();
    }

    @PutMapping("")
    public User put(@RequestBody @Validated UserPutDTO userPutDTO, BindingResult validation) throws UnauthorizedException, BadRequestException {
        Validation.validate(validation);
        return profileSvc.put(userPutDTO);
    }

    @GetMapping("/orders")
    public List<Order> getOrders() throws UnauthorizedException {
        return profileSvc.getOrders();
    }

    @GetMapping("/addresses")
    public List<Address> getAddresses() throws UnauthorizedException {
        return profileSvc.getAddresses();
    }

    @PatchMapping("/change-password")
    public ConfirmRes changePassword(@RequestBody @Validated ChangePasswordDTO changePasswordDTO, BindingResult validation) throws UnauthorizedException, BadRequestException {
        Validation.validate(validation);
        return profileSvc.changePassword(changePasswordDTO.oldPassword(), changePasswordDTO.newPassword());
    }

    @GetMapping("/is-logged-in")
    public IsLoggedInRes isLoggedIng() {
        return new IsLoggedInRes(true);
    }

    @GetMapping("get-authorities")
    public AuthoritiesRes getAuthorities() throws UnauthorizedException {
        return new AuthoritiesRes(profileSvc.getAuthorities());
    }

    @GetMapping("get-admin-userid")
    public AdminUserIdRes getAdminUserId() throws NotFoundException {
        return profileSvc.getAdminUserId();
    }

    // MESSAGES

    @GetMapping("/message/{id}/set-read")
    public Message setMessageRead(@PathVariable UUID id) throws NotFoundException, UnauthorizedException {
        return messageSvc.setMessageAsReadById(id);
    }

}
