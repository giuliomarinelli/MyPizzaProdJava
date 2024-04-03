package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID>, PagingAndSortingRepository<Menu, UUID> {
    @Query("SELECT m FROM Menu m WHERE m.item.id = :itemId")
    public Optional<Menu> findByItemId(UUID itemId);
}
