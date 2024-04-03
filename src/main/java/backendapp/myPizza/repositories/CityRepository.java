package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    public Optional<City> findByNameAndProvinceCode(String name, String provinceCode);

    @Query(value = "SELECT * FROM cities WHERE LOWER(name) LIKE CONCAT('%',LOWER(:q),'%') LIMIT :limit", nativeQuery = true)
    public List<City> search(String q, int limit);
}
