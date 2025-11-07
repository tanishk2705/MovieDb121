package com.CineTrust.config;

import com.CineTrust.repository.UserLoginsRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.annotation.Timed;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;
    private final UserLoginsRepository userLoginsRepository;

    public JwtUtils(UserLoginsRepository userLoginsRepository) {
        this.userLoginsRepository = userLoginsRepository;
    }

    /**
     * Generate JWT token for a given subject (usually user's email) and claims.
     */
    @Timed(value = "jwt.generate", description = "Time taken to generate JWT token")
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        log.info("Generating JWT for subject: {}", subject);
        log.debug("JWT claims: {}", claims);

        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            log.info("JWT generated successfully for subject: {}", subject);
            return token;
        } catch (JwtException e) {
            log.error("Error while generating JWT for subject: {}", subject, e);
            throw e;
        }
    }

    /**
     * Parse and validate JWT token.
     * Throws JwtException if token is invalid or expired.
     */
    @Timed(value = "jwt.parse", description = "Time taken to parse and validate JWT token")
    public Jws<Claims> parseToken(String token) throws JwtException {
        try {
            log.debug("Parsing JWT token...");
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);

            log.info("JWT parsed successfully. Subject: {}", claimsJws.getBody().getSubject());
            return claimsJws;

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired for subject: {}", e.getClaims().getSubject());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token", e);
            throw e;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Empty or invalid JWT token", e);
            throw e;
        }
    }



    /**
     *  Extract expiration date from JWT token (for DB persistence).
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (JwtException e) {
            log.error("Failed to extract expiration date from JWT", e);
            throw e;
        }
    }



}
