package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.Topping;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ToppingRepository extends JpaRepository<Topping, UUID> {
    public Optional<Topping> findByName(String name);

    @Query("SELECT t.name FROM Topping t")
    public List<String> findAllToppingNames();

    @Override
    @Query("SELECT t FROM Topping t ORDER BY t.createdAt DESC")
    public @NonNull List<Topping> findAll();
}
