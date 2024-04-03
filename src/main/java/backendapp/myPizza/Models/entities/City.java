package backendapp.myPizza.Models.entities;

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
@Table(name = "cities")
public class City {
    @Id
    private int id;

    private String name;

    private String provinceCode;

    private String provinceName;

    private String region;

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city")
//    List<Address> addresses = new ArrayList<>();

    public City(int id, String name, String provinceCode, String provinceName, String region) {
        this.id = id;
        this.name = name;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.region = region;
    }
}
