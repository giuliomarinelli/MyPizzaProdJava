package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.Product;
import backendapp.myPizza.Models.entities.Topping;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, PagingAndSortingRepository<Product, UUID> {

    @Override
    @Query("SELECT p FROM Product p ORDER BY createdAt DESC")
    public @NonNull Page<Product> findAll(@NonNull Pageable pageable);

    public Optional<Product> findByName(String name);

    @Query("SELECT DISTINCT(p.category.name) FROM Product p")
    public List<String> getAllCategoryNames();
}
