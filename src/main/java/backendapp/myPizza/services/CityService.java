package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.City;
import backendapp.myPizza.Models.resDTO.CityAutocomplete;
import backendapp.myPizza.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRp;

    public List<CityAutocomplete> search(String q, int limit) {
        List<City> cities = cityRp.search(q, limit);
        List<CityAutocomplete> cityAutocompletes = new ArrayList<>();
        for (City city : cities) {
            String autocomplete = city.getName() + " (" + city.getProvinceCode() + ")";
            cityAutocompletes.add(new CityAutocomplete(autocomplete, city.getName(), city.getProvinceCode()));
        }
        return cityAutocompletes;
    }

}
