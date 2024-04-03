package backendapp.myPizza.Models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;


    @OneToOne
    @JoinColumn(nullable = false, name = "item_id")
    private MenuItem item;

    public Menu(MenuItem item) {
        this.item = item;
    }
}
