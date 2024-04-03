package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.ToppingRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToppingRefRepository extends JpaRepository<ToppingRef, UUID> {
}
