package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.TokenPairType;
import backendapp.myPizza.Models.reqDTO.LoginDTO;
import backendapp.myPizza.Models.reqDTO.UserPostDTO;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.Models.resDTO.TokenPair;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.exceptions.Validation;
import backendapp.myPizza.services.AuthService;
import com.cloudinary.http44.api.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authSvc;

    @Value("${cookie.domain}")
    private String cookieDomain;

    @PostMapping("/register")
    public User register(@RequestBody @Validated UserPostDTO userPostDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return authSvc.register(userPostDTO);
    }

    @PostMapping("/login")
    public ConfirmRes login(@RequestBody @Validated LoginDTO loginDTO, BindingResult validation, HttpServletResponse res) throws UnauthorizedException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, BadRequestException {



        Validation.validate(validation);
        boolean restore = loginDTO.restore();
        Map<TokenPairType, TokenPair> tokenMap = authSvc.login(loginDTO.email(), loginDTO.password(), loginDTO.restore());
        TokenPair httpTokens = tokenMap.get(TokenPairType.HTTP);
        TokenPair wsTokens = tokenMap.get(TokenPairType.WS);
        Cookie accessToken = new Cookie("__access_tkn", httpTokens.getAccessToken());
        accessToken.setHttpOnly(true);
        accessToken.setDomain(cookieDomain);
        accessToken.setPath("/");
        accessToken.setSecure(true);
        Cookie refreshToken = new Cookie("__refresh_tkn", httpTokens.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setDomain(cookieDomain);
        refreshToken.setPath("/");
        refreshToken.setSecure(true);
        Cookie wsAccessToken = new Cookie("__ws_access_tkn", wsTokens.getAccessToken());
        wsAccessToken.setHttpOnly(true);
        wsAccessToken.setDomain(cookieDomain);
        wsAccessToken.setPath("/");
        wsAccessToken.setSecure(true);
        Cookie wsRefreshToken = new Cookie("__ws_refresh_tkn", wsTokens.getRefreshToken());
        wsRefreshToken.setHttpOnly(true);
        wsRefreshToken.setDomain(cookieDomain);
        wsRefreshToken.setPath("/");
        wsRefreshToken.setSecure(true);
        if (restore) {
            accessToken.setMaxAge(15778800);
            refreshToken.setMaxAge(15778800);
            wsAccessToken.setMaxAge(15778800);
            wsRefreshToken.setMaxAge(15778800);
        }
        res.addCookie(accessToken);
        res.addCookie(refreshToken);
        res.addCookie(wsAccessToken);
        res.addCookie(wsRefreshToken);
        return new ConfirmRes("Authenticated successfully", HttpStatus.OK);
    }
    @GetMapping("/logout")
    public ConfirmRes logout(HttpServletResponse res) {
        Cookie accessToken = new Cookie("__access_tkn", null);
        Cookie refreshToken = new Cookie("__refresh_tkn", null);
        Cookie wsAccessToken = new Cookie("__ws_access_tkn", null);
        Cookie wsRefreshToken = new Cookie("__ws_refresh_tkn", null);
        accessToken.setMaxAge(0);
        refreshToken.setMaxAge(0);
        wsAccessToken.setMaxAge(0);
        wsRefreshToken.setMaxAge(0);
        accessToken.setPath("/");
        refreshToken.setPath("/");
        wsAccessToken.setPath("/");
        wsRefreshToken.setPath("/");
        res.addCookie(accessToken);
        res.addCookie(refreshToken);
        res.addCookie(wsAccessToken);
        res.addCookie(wsRefreshToken);
        return new ConfirmRes("Logout successfully", HttpStatus.OK);
    }

}



