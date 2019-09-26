package com.seokwon.kim.quiz.bank.authentication.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;

@Component @Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    // 5hrs
    private static final int EXPIRED_TIME = 5 * 60 * 60 * 1000;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_TIME))
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(secretKey.getBytes(Charset.forName("UTF-8"))))
                .compact();
    }
    public String getUsername(final String token) {
        return parseToken(token, Base64.getEncoder().encode(secretKey.getBytes(Charset.forName("UTF-8")))).getSubject();
    }
    private static final Claims parseToken(final String token, final byte[] key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
    }
    public boolean validateToken(final String token) {
        try {
            Assert.notNull(token);
            Jwts.parser().setSigningKey(
                    Base64.getEncoder().encode(secretKey.getBytes(Charset.forName("UTF-8")))
            ).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
