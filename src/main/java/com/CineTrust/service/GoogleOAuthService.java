package com.CineTrust.service;

import com.CineTrust.client.GoogleOAuthClient;
import com.CineTrust.config.JwtUtils;
import com.CineTrust.entity.Role;
import com.CineTrust.entity.Status;
import com.CineTrust.entity.User;
import com.CineTrust.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.micrometer.core.annotation.Timed;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;


@Service
public class GoogleOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthService.class);

    private final UserRepository userRepository;
    private final GoogleOAuthClient googleClient;
    private final JwtUtils jwtUtils;
    private final UserSessionService userSessionService;

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.redirectUri}")
    private String redirectUri;

    public GoogleOAuthService(UserRepository userRepository, GoogleOAuthClient googleClient, JwtUtils jwtUtils, UserSessionService userSessionService) {
        this.userRepository = userRepository;
        this.googleClient = googleClient;
        this.jwtUtils = jwtUtils;
        this.userSessionService = userSessionService;
    }

    /**
     * Build Google login URL
     */
    @Timed(value = "oauth.buildLoginUrl", description = "Time taken to build Google OAuth login URL")
    public String   buildLoginUrl() {
        log.info("Building Google OAuth login URL for clientId={}", clientId);
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid email profile" +
                "&prompt=consent";
    }

    /**
     * Handle Google callback after user authentication
     */
    @Timed(value = "oauth.handleCallback", description = "Time taken to handle Google OAuth callback")
    public Map<String, Object> handleGoogleCallback(String code) {
        log.info("Handling Google OAuth callback with code={}", code);

        Map<String, Object> tokenResp = googleClient.exchangeCodeForToken(code);
        log.debug("Token response received successfully for code={}", code);

        Map<String, Object> userInfo = googleClient.fetchUserInfo((String) tokenResp.get("access_token"));
        log.info("Fetched user info from Google: {}", userInfo.get("email"));

        User user = saveOrGetUser(userInfo);
        log.info("User authenticated: {}", user.getEmail());

        String jwt = generateJwt(user);
        log.debug("Generated JWT for user={}", user.getEmail());

        return Map.of(
                "jwt", jwt,
                "email", user.getEmail(),
                "name", user.getUsername(),
                "role", user.getRole()
        );
    }

    private User saveOrGetUser(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("New user detected. Saving user with email={}", email);
                    return userRepository.save(
                            User.builder()
                                    .email(email)
                                    .username(name)
                                    .role(Role.USER)
                                    .status(Status.ACTIVE)
                                    .build()
                    );
                });
    }

    private String generateJwt(User user) {
        Map<String, Object> claims = Map.of(
                "email", user.getEmail(),
                "role", user.getRole().name()
        );

        log.debug("Generating JWT for user email={}", user.getEmail());

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail(), claims);

        // Extract expiration from JWT or define manually
        LocalDateTime expirationTime = jwtUtils.getExpirationDateFromToken(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Create session entry in DB
        userSessionService.createSession(user, token, expirationTime);

        return token;
    }



}
