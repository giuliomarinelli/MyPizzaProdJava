package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.WorkSession;
import backendapp.myPizza.Models.reqDTO.StartSessionDTO;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.Models.resDTO.DeliveryTimeRes;
import backendapp.myPizza.Models.resDTO.IsThereAnActiveSessionRes;
import backendapp.myPizza.Models.resDTO.TimeIntervalsRes;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.Validation;
import backendapp.myPizza.services._SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-session")

public class SessionController {

    @Autowired
    _SessionService _sessionSvc;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-active-session")
    public WorkSession getActiveSession() throws BadRequestException {
        return _sessionSvc.getActiveSession();
    }

    @GetMapping("/is-there-an-active-session")
    public IsThereAnActiveSessionRes isThereAnActiveSession() {
        return new IsThereAnActiveSessionRes(_sessionSvc.isThereAnActiveSession());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/start-new-session")
    public WorkSession startNewSession(@RequestBody @Validated StartSessionDTO startSessionDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return _sessionSvc.startNewSession(startSessionDTO);
    }

    @GetMapping("/get-delivery-times")
    public DeliveryTimeRes getDeliveryTimes() {
        return new DeliveryTimeRes(_sessionSvc.getActiveSessionDeliveryTimes());
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-active-session-time-intervals")
    public TimeIntervalsRes getActiveSessionTimeIntervals() {
        return new TimeIntervalsRes(_sessionSvc.getActiveSessionTimeIntervals());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/close")
    public ConfirmRes closeActiveSession() throws BadRequestException {
        return _sessionSvc.closeActiveSession();
    }




}
