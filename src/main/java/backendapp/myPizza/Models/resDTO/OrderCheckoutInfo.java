package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.entities.Address;
import backendapp.myPizza.Models.entities.OrderSet;
import backendapp.myPizza.Models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCheckoutInfo {

    private UUID orderId;

    private Address address;

    private List<OrderSet> orderSets;

    private OrderStatus status;

    private double deliveryCost;

    private double totalAmount;

}
