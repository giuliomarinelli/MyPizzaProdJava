package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderSetDTO(
        @NotNull(message = "'productId' field is required")
        UUID productId,
        @NotNull(message = "'quantity' is required")
        @Min(value = 1, message = "'quantity' must be an integer number major than 0")
        Integer quantity
) {}
