package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.entities.Topping;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddToppingsDTO(
        @NotNull(message = "'toppings' list is required")
        List<Topping> toppings
) {

}
