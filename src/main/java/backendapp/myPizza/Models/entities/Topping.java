package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.ItemType;
import backendapp.myPizza.Models.enums.ToppingType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "toppings")
public class Topping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(unique = true)
    private String name;

    private double price;

    private long createdAt;

    private ToppingType type;


    @Transient
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "toppings", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Topping(String name, double price, ToppingType type) {
        this.name = name;
        this.price = price;
        this.type = type;
        createdAt = System.currentTimeMillis();
    }
}
