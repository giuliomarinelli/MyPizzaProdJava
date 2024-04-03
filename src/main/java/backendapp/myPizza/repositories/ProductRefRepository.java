package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.ProductRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRefRepository extends JpaRepository<ProductRef, UUID> {
}
