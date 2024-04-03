package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductDTO(
        @NotBlank(message = "'name' is required and must not be empty")
        String name,
        @NotNull(message = "'basePrice' is required")
        @Min(value = 0, message = "'basePrice' must be a non negative number")
        Double basePrice,
        @NotNull(message = "'toppings' is required")
        List<String> toppings,

        @NotBlank(message = "'category' is required and must not be empty")
        String category

) {}
