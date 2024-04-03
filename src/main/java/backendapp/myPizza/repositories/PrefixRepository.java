package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.InternationalPrefix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrefixRepository extends JpaRepository<InternationalPrefix, String> {
    @Query("SELECT p FROM InternationalPrefix p ORDER BY p.prefix ASC")
    public List<InternationalPrefix> findAllOrderAsc();
}
