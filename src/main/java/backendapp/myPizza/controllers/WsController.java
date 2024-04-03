package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.TokenPairType;
import backendapp.myPizza.Models.enums.TokenType;
import backendapp.myPizza.Models.resDTO.IsWsAuthValid;
import backendapp.myPizza.Models.resDTO.TokenPair;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.security.JwtUtils;
import backendapp.myPizza.services.AuthUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/ws")
@Log4j2
public class WsController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUserService authUserSvc;

    @Value("${cookie.domain}")
    private String cookieDomain;

    @GetMapping("/is-ws-auth-valid-or-refresh")
    public IsWsAuthValid isWsAuthValidOrRefresh(HttpServletRequest req, HttpServletResponse res) throws UnauthorizedException {
        if (req.getCookies() == null) return new IsWsAuthValid(false);
        Cookie[] cookies = req.getCookies();
        String wsAccessToken = "";
        String wsRefreshToken = "";
        Optional<Cookie> wsAccessTokenOpt = Arrays.stream(cookies).filter(c -> c.getName().equals("__ws_access_tkn")).findFirst();
        Optional<Cookie> wsRefreshTokenOpt = Arrays.stream(cookies).filter(c -> c.getName().equals("__ws_refresh_tkn")).findFirst();
        if (wsAccessTokenOpt.isPresent()) wsAccessToken = wsAccessTokenOpt.get().getValue();
        if (wsRefreshTokenOpt.isPresent()) wsRefreshToken = wsRefreshTokenOpt.get().getValue();
        if (wsAccessToken.isEmpty() || wsRefreshToken.isEmpty()) return new IsWsAuthValid(false);
        boolean isWsAccessTokenValid = jwtUtils.verifyWsAccessToken(wsAccessToken);
        if (isWsAccessTokenValid) {
            return new IsWsAuthValid(true);
        } else {
            if (jwtUtils.verifyWsRefreshToken(wsRefreshToken)) {
                boolean restore = jwtUtils.getRestoreFromWsRefreshToken(wsRefreshToken);
                UUID userId = jwtUtils.extractUserIdFromWsRefreshToken(wsRefreshToken);
                User u = authUserSvc.findUserById(userId);
                TokenPair newTokens = new TokenPair(jwtUtils.generateToken(u, TokenType.WS_ACCESS, restore),
                        jwtUtils.generateToken(u, TokenType.WS_REFRESH, restore), TokenPairType.WS);
                Cookie c_wsAccessToken = new Cookie("__ws_access_tkn", newTokens.getAccessToken());
                Cookie c_wsRefreshToken = new Cookie("__ws_refresh_tkn", newTokens.getRefreshToken());
                log.info("refreshed web socket auth for userId=" + userId);
                c_wsAccessToken.setPath("/");
                c_wsAccessToken.setHttpOnly(true);
                c_wsAccessToken.setDomain(cookieDomain);
                c_wsAccessToken.setSecure(true);
                c_wsRefreshToken.setPath("/");
                c_wsRefreshToken.setHttpOnly(true);
                c_wsRefreshToken.setDomain(cookieDomain);
                c_wsRefreshToken.setSecure(true);
                if (restore) {
                    c_wsAccessToken.setMaxAge(15778800);
                    c_wsRefreshToken.setMaxAge(15778800);
                }
                res.addCookie(c_wsAccessToken);
                res.addCookie(c_wsRefreshToken);
                return new IsWsAuthValid(true);
            } else {
                return  new IsWsAuthValid(false);
            }
        }
    }
}
