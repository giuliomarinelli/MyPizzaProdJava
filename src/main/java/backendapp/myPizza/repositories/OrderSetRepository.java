package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.OrderSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderSetRepository extends JpaRepository<OrderSet, UUID> {
}
