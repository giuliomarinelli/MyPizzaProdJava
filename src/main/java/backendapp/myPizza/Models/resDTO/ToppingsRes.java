package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.entities.Topping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToppingsRes {
    private List<Topping> toppings;
}
