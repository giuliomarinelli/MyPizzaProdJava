package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.ToppingType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "toppings_ref")
public class ToppingRef {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String name;

    private double price;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "toppingsRef", fetch = FetchType.EAGER)
//    private List<ProductRef> productsRef = new ArrayList<>();

    public ToppingRef(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
