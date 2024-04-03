package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.entities.Address;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendOrderDTO(

        @NotNull(message = "'orderId' is required")
        UUID orderId,

        @NotNull(message = "'asap' is required")
        Boolean asap,

        @NotNull(message = "'expectedDeliveryTime' is required")
        @Min(value = 0, message = "'expectedDeliveryTime' must not be a negative number")
        Long expectedDeliveryTime,

        @NotNull(message = "'address' is required")
        Address address
) {}
