package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@Data
@Table(name = "products")
public class Product extends MenuItem {

    @Column(unique = true)
    private String name;

    ItemType type = ItemType.PRODUCT;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "products_toppings",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_id")
    )
    private List<Topping> toppings = new ArrayList<>();

    private double basePrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private long createdAt;


    @Transient
    private double price;

    public Product(String name, double basePrice, Category category) {
        this.name = name;
        this.basePrice = basePrice;
        this.category = category;
        createdAt = System.currentTimeMillis();
    }

    public void setProductTotalAmount() {
        price = toppings.stream().mapToDouble(Topping::getPrice).sum() + basePrice;
    }
}
