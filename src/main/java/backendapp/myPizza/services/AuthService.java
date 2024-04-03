package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.Address;
import backendapp.myPizza.Models.entities.City;
import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.TokenPairType;
import backendapp.myPizza.Models.enums.TokenType;
import backendapp.myPizza.Models.enums.UserScope;
import backendapp.myPizza.Models.reqDTO.GuestUserDTO;
import backendapp.myPizza.Models.reqDTO.UserPostDTO;
import backendapp.myPizza.Models.resDTO.TokenPair;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.repositories.AddressRepository;
import backendapp.myPizza.repositories.CityRepository;
import backendapp.myPizza.repositories.UserRepository;
import backendapp.myPizza.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private AddressRepository addressRp;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRp;

    @Autowired
    private CityRepository cityRp;

    public User generateGuestUser(GuestUserDTO guestUserDTO) {
        return userRp.save(User.generateGuestUser(guestUserDTO.firstName(), guestUserDTO.lastName(), guestUserDTO.email(), guestUserDTO.phoneNumber()));
    }

    public User register(UserPostDTO userPostDTO) throws BadRequestException {
        City city = cityRp.findByNameAndProvinceCode(userPostDTO.address().city(), userPostDTO.address().province())
                .orElseThrow(
                        () -> new BadRequestException("City entered doesn't exist")
                );

        User u = new User(userPostDTO.firstName(), userPostDTO.lastName(), userPostDTO.email(),
                encoder.encode(userPostDTO.password()), userPostDTO.phoneNumber(), userPostDTO.gender());
        try {
            userRp.save(u);
        } catch (DataIntegrityViolationException e) {
            if (userRp.findAllEmails().contains(u.getEmail()) && userRp.findAllPhoneNumbers().contains(u.getPhoneNumber())) {
                throw new BadRequestException("Email and phoneNumber already exist. Cannot create");
            } else if (userRp.findAllEmails().contains(u.getEmail())) {
                throw new BadRequestException("Email already exists. Cannot create");
            } else if (userRp.findAllPhoneNumbers().contains(u.getPhoneNumber())) {
                throw new BadRequestException("phoneNumber already exists. Cannot create");
            }
        }
        Address address = new Address(userPostDTO.address().road(), userPostDTO.address().civic(), city, u);
        address.set_default(true);
        addressRp.save(address);
        u.getAddresses().add(address);
        return u;
    }

    public Map<TokenPairType, TokenPair> login(String email, String password, boolean restore) throws UnauthorizedException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        User u = userRp.findByEmail(email).orElseThrow(
                () -> new UnauthorizedException("Email and/or password are incorrect")
        );
        if (u.getScope().contains(UserScope.GUEST)) throw new UnauthorizedException("Email and/or password are incorrect");
        if (!encoder.matches(password, u.getHashPassword()))
            throw new UnauthorizedException("Email and/or password are incorrect");
        Map<TokenPairType, TokenPair> tokenMap = new HashMap<>();
        TokenPair httpTokenPair = new TokenPair(jwtUtils.generateToken(u, TokenType.ACCESS, restore),
                jwtUtils.generateToken(u, TokenType.REFRESH, restore), TokenPairType.HTTP);
        tokenMap.put(TokenPairType.HTTP, httpTokenPair);
        TokenPair wsTokenPair = new TokenPair(jwtUtils.generateToken(u, TokenType.WS_ACCESS, restore),
                jwtUtils.generateToken(u, TokenType.WS_REFRESH, restore), TokenPairType.WS);
        tokenMap.put(TokenPairType.WS, wsTokenPair);
        return tokenMap;
    }


    public User findByUserId(UUID userId) throws UnauthorizedException {
        return userRp.findById(userId).orElseThrow(
                () -> new UnauthorizedException("Invalid access token")
        );
    }
}
