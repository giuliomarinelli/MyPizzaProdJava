package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthUserService {
    @Autowired
    private UserRepository userRp;

    public User findUserById(UUID id) throws UnauthorizedException {
        return userRp.findById(id).orElseThrow(
                () -> new UnauthorizedException("Invalid access token")
        );
    }

}
