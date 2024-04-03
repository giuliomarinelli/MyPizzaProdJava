package backendapp.myPizza.config;

import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.UserScope;
import backendapp.myPizza.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Component
public class ProdSetAdmin implements CommandLineRunner {

    @Autowired
    private UserRepository userRp;


    @Override
    public void run(String... args) throws Exception {
        User admin = userRp.findById(UUID.fromString("a7065291-d649-4077-b27b-e57d6830bb05")).get();
        admin.getScope().add(UserScope.ADMIN);
        admin.setMessagingUsername("MyPizza");
        userRp.save(admin);


    }
}
