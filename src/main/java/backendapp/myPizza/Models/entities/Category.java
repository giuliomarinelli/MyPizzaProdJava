package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "categories")
public class Category extends MenuItem {

    @Column(unique = true)
    private String name;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
//    @JsonIgnore
//    private List<Product> products = new ArrayList<>();

    ItemType type = ItemType.CATEGORY;

    public Category(String name) {
        this.name = name;
    }
}
