package com.example.user_management_api.secutity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    @Value("${app.jwt.secret}")
    private String jwtSecretString;
    @Value("${app.jwt.expirationMs}")
    private Long accessTokenDurationMs;

    public String generateJwtToken(String email) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(accessTokenDurationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSignInKey())
                .compact();
    }


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();

    }

    public boolean validateJwtToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException",expiredJwtException);
        } catch (UnsupportedJwtException exception){
            log.error("UnsupportedJwtException",exception);
        } catch (MalformedJwtException exception){
            log.error("MalformedJwtException",exception);
        } catch (SecurityException exception){
            log.error("SecurityException",exception);
        } catch (Exception exception){
            log.error("Invalid token",exception);
        }
        return false;
    }
}
