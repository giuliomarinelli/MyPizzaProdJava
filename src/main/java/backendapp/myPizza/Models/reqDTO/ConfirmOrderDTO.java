package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmOrderDTO (
        @NotNull(message = "'orderId' is required")
        UUID orderId,

        @NotNull(message = "'timeIntervalId' is required")
        UUID timeIntervalId
){
}
