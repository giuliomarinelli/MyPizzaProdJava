package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInitRes {
    private UUID orderId;

    private OrderStatus status;
}
