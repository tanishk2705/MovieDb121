package com.CineTrust.service;


import com.CineTrust.entity.Status;
import com.CineTrust.entity.User;
import com.CineTrust.entity.UserLogins;
import com.CineTrust.repository.UserLoginsRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserSessionService {

    private final UserLoginsRepository userLoginsRepository;

    public UserSessionService(UserLoginsRepository userLoginsRepository) {
        this.userLoginsRepository = userLoginsRepository;
    }

    /**
     * When a user logs in (token generated)
     */
    public void createSession(User user, String token, LocalDateTime expirationTime) {
        UserLogins session = UserLogins.builder()
                .user(user)
                .token(token)
                .status(Status.ACTIVE)
                .expirationDate(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        userLoginsRepository.save(session);
    }

    /**
     * Check if token is valid (for JwtAuthFilter)
     */
    public boolean isTokenValid(String token) {
        Optional<UserLogins> sessionOpt = userLoginsRepository.findByToken(token);

        return sessionOpt
                .filter(session ->
                        session.getStatus() == Status.ACTIVE &&
                                session.getExpirationDate() != null &&
                                session.getExpirationDate().after(new Date())
                )
                .isPresent();
    }

    /**
     * Invalidate token on logout
     */
    public boolean invalidateToken(String token) {
        return userLoginsRepository.findByToken(token)
                .map(session -> {
                    session.setStatus(Status.SUSPENDED);
                    userLoginsRepository.save(session);
                    return true;
                })
                .orElse(false);
    }
}
