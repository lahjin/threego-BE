package com.kkultrip.threego.config.security.jwt;

import com.kkultrip.threego.config.security.auth.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final String jwtSecret;

    private final int jwtExp;

    public JwtUtils(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.exp}") int jwtExp) {
        this.jwtSecret = jwtSecret;
        this.jwtExp = jwtExp * 60* 1000;
    }

    public String createJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject("ThreeGo >  " + userPrincipal.getUsername() + "'s JWT")
                .setIssuedAt(new Date())
                .claim("id", userPrincipal.getId())
                .claim("username", userPrincipal.getUsername())
                .claim("nickname", userPrincipal.getNickname())
                .claim("roles", userPrincipal.getAuthorities())
                .setExpiration(new Date(new Date().getTime() + jwtExp))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("username",String.class);
    }

    public Long getUserIdFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("id",Long.class);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            logger.info("????????? JWT ???????????????.");
        } catch (ExpiredJwtException e) {
            logger.info("????????? JWT ???????????????.");
        } catch (UnsupportedJwtException e) {
            logger.info("???????????? ?????? JWT ???????????????.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT ????????? ?????????????????????.");
        }
        return false;
    }
}
