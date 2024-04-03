package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.SocketIO.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u")
    public List<String> findAllEmails();

    @Query("SELECT u.phoneNumber FROM User u")
    public List<String> findAllPhoneNumbers();



}
