package backendapp.myPizza.security;


import backendapp.myPizza.Models.entities.User;
import backendapp.myPizza.Models.enums.TokenPairType;
import backendapp.myPizza.Models.resDTO.ErrorRes;
import backendapp.myPizza.Models.resDTO.TokenPair;
import backendapp.myPizza.exceptions.UnauthorizedException;
import backendapp.myPizza.services.AuthService;
import backendapp.myPizza.services.AuthUserService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.UUID;

@Component
//@Log4j2
public class JwtFilter extends OncePerRequestFilter {

    @Value(("${cookie.domain}"))
    private String cookieDomain;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUserService authUserSvc;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {

            if (req.getCookies() == null) {
                throw new UnauthorizedException("No provided access and refresh tokens");
            }
            Cookie[] cookies = req.getCookies();
            TokenPair tokens = new TokenPair();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("__access_tkn")) tokens.setAccessToken(cookie.getValue());
                if (cookie.getName().equals("__refresh_tkn")) tokens.setRefreshToken(cookie.getValue());
            }
            if (tokens.getAccessToken() == null) throw new UnauthorizedException("No provided access token");
            if (tokens.getRefreshToken() == null) throw new UnauthorizedException("No provided refresh token");
            try {
                jwtUtils.verifyAccessToken(tokens.getAccessToken());
                UUID userId = jwtUtils.extractUserIdFromAccessToken(tokens.getAccessToken());
                User u = authUserSvc.findUserById(userId);
//                log.info("Authorized userId=" + userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, cookies, u.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(req, res);
            } catch (ExpiredJwtException e) {
                boolean restore = jwtUtils.verifyRefreshToken(tokens.getRefreshToken());
                TokenPair newTokens = jwtUtils.generateTokenPair(tokens.getRefreshToken(), TokenPairType.HTTP, restore);
                Cookie accessToken = new Cookie("__access_tkn", newTokens.getAccessToken());
                accessToken.setHttpOnly(true);
                accessToken.setDomain(cookieDomain);
                accessToken.setPath("/");
                accessToken.setSecure(true);
                Cookie refreshToken = new Cookie("__refresh_tkn", newTokens.getRefreshToken());
                refreshToken.setHttpOnly(true);
                refreshToken.setDomain(cookieDomain);
                refreshToken.setPath("/");
                refreshToken.setSecure(true);
                if (restore) {
                    accessToken.setMaxAge(15778800);
                    refreshToken.setMaxAge(15778800);
                }
                res.addCookie(accessToken);
                res.addCookie(refreshToken);
                UUID userId = jwtUtils.extractUserIdFromAccessToken(newTokens.getAccessToken());
                User u = authUserSvc.findUserById(userId);
//                log.info("Authorized userId=" + userId + " after refresh");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, cookies, u.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(req, res);
            }


        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(mapper.writeValueAsString(
                    new ErrorRes("Unauthorized", e.getMessage(), HttpStatus.UNAUTHORIZED))
            );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        return new AntPathMatcher().match("/tomcat/auth/**", req.getServletPath())
                || new AntPathMatcher().match("/tomcat/public/**", req.getServletPath())
                || new AntPathMatcher().match("/tomcat/api/user-profile/get-admin-userid", req.getServletPath())
                || new AntPathMatcher().match("/tomcat/api/work-session/is-there-an-active-session", req.getServletPath())
                || new AntPathMatcher().match("/tomcat/api/work-session/get-delivery-times", req.getServletPath())
                || new AntPathMatcher().match("/auth/**", req.getServletPath())
                || new AntPathMatcher().match("/public/**", req.getServletPath())
                || new AntPathMatcher().match("/api/user-profile/get-admin-userid", req.getServletPath())
                || new AntPathMatcher().match("/api/work-session/is-there-an-active-session", req.getServletPath())
                || new AntPathMatcher().match("/api/work-session/get-delivery-times", req.getServletPath());
    }


}
