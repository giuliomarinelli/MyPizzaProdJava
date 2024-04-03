package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, PagingAndSortingRepository<Order, UUID> {
}
