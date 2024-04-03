package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@NoArgsConstructor
@Data
@Table(name = "products_ref")
public class ProductRef {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "products_ref_toppings_ref",
            joinColumns = @JoinColumn(name = "product_ref_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_ref_id")
    )
    private List<ToppingRef> toppingsRef = new ArrayList<>();

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "productRef")
//    private List<OrderSet> orderSets = new ArrayList<>();


    private double price;


    public ProductRef(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
