package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.*;
import backendapp.myPizza.Models.enums.OrderStatus;
import backendapp.myPizza.Models.reqDTO.OrderInitDTO;
import backendapp.myPizza.Models.reqDTO.OrderSetDTO;
import backendapp.myPizza.Models.reqDTO.SendOrderDTO;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.Models.resDTO.OrderCheckoutInfo;
import backendapp.myPizza.Models.resDTO.OrderInitRes;
import backendapp.myPizza.SocketIO.services.MessageService;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.repositories.*;
import backendapp.myPizza.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRp;

    @Autowired
    private OrderSetRepository orderSetRp;

    @Autowired
    private ProductRepository productRp;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRp;

    @Autowired
    private ToppingRefRepository toppingRefRp;

    @Autowired
    private ProductRefRepository productRefRp;

    @Autowired
    private _SessionService _session;

    @Autowired
    private TimeIntervalRepository timeIntervalRp;

    @Autowired
    private MessageService messageSvc;

    public OrderInitRes orderInit(OrderInitDTO orderInitDTO) throws BadRequestException {
        Order order = new Order();
        order.setStatus(OrderStatus.INIT);
        orderRp.save(order);
        if (orderInitDTO.orderSetsDTO().isEmpty()) throw new BadRequestException("Order sent is empty");
        List<OrderSet> orderSets = new ArrayList<>();
        for (OrderSetDTO o : orderInitDTO.orderSetsDTO()) {
            Product p = productRp.findById(o.productId()).orElseThrow(
                    () -> new BadRequestException("Product with id='" + o.productId() + "' doesn't exist")
            );
            p.setProductTotalAmount();
            if (o.quantity() <= 0)
                throw new BadRequestException("Product with id='" + o.productId() + "': quantity must be an integer number major than 0");
            ProductRef pr = new ProductRef(p.getName(), p.getPrice());
            for (Topping t : p.getToppings()) {
                ToppingRef tr = new ToppingRef(t.getName(), t.getPrice());
                toppingRefRp.save(tr);
                pr.getToppingsRef().add(tr);
            }
            productRefRp.save(pr);
            OrderSet orderSet = new OrderSet(pr, o.quantity());
            orderSet.setOrder(order);
            orderSets.add(orderSet);
        }
        orderSetRp.saveAll(orderSets);
        orderRp.save(order);
        return new OrderInitRes(order.getId(), order.getStatus());
    }

    public OrderCheckoutInfo getClientOrderInit(Boolean guest, Order order) throws UnauthorizedException, BadRequestException {
        if (guest) {
            // da implementare
            return null;
        } else {
            UUID userId = jwtUtils.extractUserIdFromReq();
            assert userRp.findById(userId).isPresent();
            User user = userRp.findById(userId).get();
            Address address = user.getAddresses().stream().filter(Address::is_default).findFirst()
                    .orElseThrow(
                            () -> new BadRequestException("Cannot find default address, please set a default address before retrying")
                    );
            OrderStatus status = order.getStatus();
            List<OrderSet> orderSets = order.getOrderSets();
            double deliveryCost = order.getDeliveryCost();
            double totalAmount = 0;
            for (OrderSet os : orderSets) {
                totalAmount += os.getQuantity() * os.getProductRef().getPrice();
            }
            totalAmount += deliveryCost;

            return new OrderCheckoutInfo(order.getId(), address, orderSets, status, deliveryCost, totalAmount);
        }

    }

    public ConfirmRes sendOrder(SendOrderDTO sendOrderDTO, boolean guest) throws BadRequestException, UnauthorizedException {
        Order order = orderRp.findById(sendOrderDTO.orderId()).orElseThrow(
                () -> new BadRequestException("Order you're trying to send doesn't exist")
        );
        if (!order.getStatus().equals(OrderStatus.INIT))
            throw new BadRequestException("An order must have INIT status to be sent");
        order.setStatus(OrderStatus.PENDING);
        UUID userId = guest ? jwtUtils.extractGuestUserIdFromReq() : jwtUtils.extractUserIdFromReq();
        User user = userRp.findById(userId).orElseThrow(
                () -> new BadRequestException("User doesn't exist")
        );
        order.setUser(user);
        order.setAddress(sendOrderDTO.address());
        order.setAsap(sendOrderDTO.asap());
        order.setExpectedDeliveryTime(sendOrderDTO.expectedDeliveryTime());
        order.setOrderTime(System.currentTimeMillis());
        orderRp.save(order);
        return new ConfirmRes("Order with id='" + sendOrderDTO.orderId() + " confirmed successfully", HttpStatus.OK);
    }

    public Order getOrderById(UUID orderId) throws BadRequestException {
        return orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("order with id='" + orderId + "' doesn't exist")
        );
    }

    public ConfirmRes confirmOrder(UUID orderId, UUID timeIntervalId) throws BadRequestException {
        TimeInterval timeInterval = timeIntervalRp.findById(timeIntervalId).orElseThrow(
                () -> new BadRequestException("TimeInterval with id='" + timeIntervalId + "' doesn't exist")
        );
        Order order = orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("Order with id='" + orderId + "' doesn't exist")
        );
        order.setTimeInterval(timeInterval);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setDeliveryTime(timeInterval.getEndsAt());
        orderRp.save(order);

        return new ConfirmRes("order with id ='" + orderId + "' confirmed successfully", HttpStatus.OK);
    }



    public ConfirmRes rejectOrder(UUID orderId) throws BadRequestException {
        Order order = orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("Order you're trying to reject doesn't exist")
        );
        if (!order.getStatus().equals(OrderStatus.PENDING))
            throw new BadRequestException("An order must have PENDING status to be confirmed or rejected");
        order.setStatus(OrderStatus.REJECTED);
        orderRp.save(order);
        return new ConfirmRes("Order with id='" + orderId + " rejected", HttpStatus.OK);
    }

    public List<TimeInterval> completeOrder(UUID orderId) throws BadRequestException, NotFoundException {
        Order order = orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("Order you're trying to confirm doesn't exist")
        );
        if (!order.getStatus().equals(OrderStatus.ACCEPTED))
            throw new BadRequestException("An order must have ACCEPTED status to be confirmed or rejected");
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(System.currentTimeMillis());
        orderRp.save(order);
        return _session.getActiveSessionTimeIntervals();

    }


}
