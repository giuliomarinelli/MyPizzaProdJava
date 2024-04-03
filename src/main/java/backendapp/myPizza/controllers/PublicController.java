package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.InternationalPrefix;
import backendapp.myPizza.Models.entities.Menu;
import backendapp.myPizza.Models.entities.Order;
import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.TokenType;
import backendapp.myPizza.Models.reqDTO.GuestUserDTO;
import backendapp.myPizza.Models.reqDTO.OrderInitDTO;
import backendapp.myPizza.Models.reqDTO.SendOrderDTO;
import backendapp.myPizza.Models.resDTO.*;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.exceptions.Validation;
import backendapp.myPizza.repositories.OrderRepository;
import backendapp.myPizza.security.JwtUtils;
import backendapp.myPizza.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private PrefixService prefixSvc;

    @Autowired
    private CityService citySvc;

    @Autowired
    private MenuService menuSvc;

    @Autowired
    private OrderService orderSvc;

    @Autowired
    private OrderRepository orderRp;

    @Autowired
    private AuthService authSvc;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/menu")
    public Page<Menu> getMenu(Pageable pageable) {
        return menuSvc.getMenu(pageable);
    }

    @GetMapping("/international-prefixes")
    public List<InternationalPrefix> getInternationalPrefixes() {
        return prefixSvc.findAll();
    }

    @GetMapping("/city-autocomplete")
    public List<CityAutocomplete> getCityAutocomplete(@RequestParam String q,  @RequestParam(required = false) Integer limit) {
        int l = 10;
        if (limit != null) l = limit;
        return citySvc.search(q, l);
    }

    // -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.
    // ORDER
    @PostMapping("/order-init")
    public OrderInitRes orderInit(@RequestBody @Validated OrderInitDTO orderInitDTO, BindingResult validation, HttpServletResponse res) throws BadRequestException {
        Validation.validate(validation);
        OrderInitRes _res = orderSvc.orderInit(orderInitDTO);
        Cookie cookie = new Cookie("__order_id", _res.getOrderId().toString());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        res.addCookie(cookie);
        return _res;
    }

    @GetMapping("/get-client-order-id")
    public GetOrderIdRes getOrderId(HttpServletRequest req) throws BadRequestException {
        if (req.getCookies() == null) throw new BadRequestException("Missing __order_id cookie");
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("__order_id")).findFirst().orElseThrow(
                () -> new BadRequestException("Missing __order_id cookie")
        );
        UUID orderId;
        try {
            orderId = UUID.fromString(cookie.getValue());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID format from __order_id cookie");
        }
        orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("orderId from __order_id cookie reffers to an order that doesn't exist")
        );
        return new GetOrderIdRes(orderId);
    }

    @GetMapping("/get-client-order-init")
    public OrderCheckoutInfo getClientOrderInit(@RequestParam(required = false) Boolean guest, HttpServletRequest req) throws BadRequestException, UnauthorizedException {
        boolean _guest;

        _guest = Objects.requireNonNullElse(guest, false);

        if (req.getCookies() == null) throw new BadRequestException("Missing __order_id cookie");
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("__order_id")).findFirst().orElseThrow(
                () -> new BadRequestException("Missing __order_id cookie")
        );
        UUID orderId;
        try {
            orderId = UUID.fromString(cookie.getValue());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID format from __order_id cookie");
        }
        Order order = orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("orderId from __order_id cookie refers to an order that doesn't exist")
        );
        return orderSvc.getClientOrderInit(false, order);
    }

    @PostMapping("/send-order")
    public ConfirmRes sendOrder(@RequestParam(required = false) Boolean guest,
                                @RequestBody @Validated SendOrderDTO sendOrderDTO, BindingResult validation, HttpServletResponse res) throws UnauthorizedException, BadRequestException {
        Validation.validate(validation);
        boolean _guest = Objects.requireNonNullElse(guest, false);
        ConfirmRes conf = orderSvc.sendOrder(sendOrderDTO, _guest);
        Cookie cookie = new Cookie("__order_id", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        return conf;
    }

    @PostMapping("/get-guest-ws-auth")
    public ConfirmRes getGuestWsUser(@RequestBody GuestUserDTO guestUserDTO, HttpServletResponse res) {
        User guest = authSvc.generateGuestUser(guestUserDTO);
        String wsAccessToken = jwtUtils.generateGuestWsAccessToken(guest.getId());
        Cookie cookie = new Cookie("__ws_access_tkn", wsAccessToken);
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        res.addCookie(cookie);
        return new ConfirmRes("As a guest, you can remain connected for not more than 1 hour" +
                " to notification system", HttpStatus.OK);
    }


}
