package backendapp.myPizza.SocketIO;

import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.security.JwtUtils;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import jakarta.servlet.http.Cookie;
import lombok.extern.log4j.Log4j2;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.*;

@Component
@Log4j2
public class SocketIOAuth implements AuthorizationListener {

    @Autowired
    private JwtUtils jwtUtils;

    public static List<Cookie> cookieParser(String reqCookieHeader) {

        String[] cookies = reqCookieHeader.split(";");
        List<Cookie> parsedCookies = new ArrayList<>();
        for (String c : cookies) {
            String[] keyAndValue = c.trim().split("=");
            parsedCookies.add(new Cookie(keyAndValue[0].trim(), keyAndValue[1].trim()));
        }
        return parsedCookies;
    }

    @Override
    public boolean isAuthorized(HandshakeData data) {
        log.info("Auth Socket");
        String cookies = data.getHttpHeaders().get("Cookie");
        log.info(cookies);
        if (cookies == null) return false;
        if (cookies.isBlank()) return false;
        List<Cookie> parsedCookies = cookieParser(cookies);
        Cookie accessToken = null;

        try {
            accessToken = parsedCookies.stream().filter(c -> c.getName().equals("__ws_access_tkn")).findFirst().get();
        } catch (Exception e) {
            return false;
        }
        boolean isValid = jwtUtils.verifyWsAccessToken(accessToken.getValue());
        try {
            UUID userId = jwtUtils.extractUserIdFromWsAccessToken(accessToken.getValue());
            log.info("SOCKET: authenticated userId=" + userId);
        } catch (UnauthorizedException e) {
            log.info("Unauthorized");
            return false;
        }


        return isValid;

    }

}