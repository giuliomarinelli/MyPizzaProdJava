package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    public Optional<Category> findByName(String name);

    @Query("SELECT c.name FROM Category c")
    public List<String> getAllCategoryNames();
}
