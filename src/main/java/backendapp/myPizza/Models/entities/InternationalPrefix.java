package backendapp.myPizza.Models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "international_prefixes")
public class InternationalPrefix {
    @Id
    @Setter(AccessLevel.NONE)
    private String prefix;
}
