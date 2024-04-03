package backendapp.myPizza.Models.resDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityAutocomplete {
    private String autocomplete;
    private String name;
    private String provinceCode;
}
