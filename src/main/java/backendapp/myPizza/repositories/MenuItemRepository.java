package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
}
