package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.enums.ToppingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ToppingDTO(
        @NotBlank(message = "'name' is required and must not be empty")
        String name,

        @NotNull(message = "'price' is required")
        @Min(value = 0, message = "'price' must not be a negative number")
        Double price,

        @NotNull(message = "'type' is required")
        ToppingType type
) {}
