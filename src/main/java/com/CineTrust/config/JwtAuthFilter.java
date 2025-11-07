package com.CineTrust.config;


import com.CineTrust.service.UserSessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtils jwtUtils;
    private final UserSessionService userSessionService;


    public JwtAuthFilter(JwtUtils jwtUtils, UserSessionService userSessionService) {
        this.jwtUtils = jwtUtils;
        this.userSessionService = userSessionService;

    }

    @Override
    @Timed(value = "jwt.auth.filter", description = "Time taken by JwtAuthFilter for each request")
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();  // clean token


            /**
             *  Check if token is valid in DB
             */
            if (!userSessionService.isTokenValid(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired or logged out");
                return;
            }


            try {
                Jws<Claims> claimsJws = jwtUtils.parseToken(token);
                Claims claims = claimsJws.getBody();
                String email = claims.getSubject();
                String role = (String) claims.get("role");

                log.info("JWT validated for user: {}, role: {}", email, role);
                if (email == null || email.isEmpty()) {
                    log.error("JWT token missing subject/email");
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: missing email");
                    return;
                }

                User authUser = new User(email, "", Collections.singleton(() -> "ROLE_" + role));
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(authUser, token, authUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);


            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
