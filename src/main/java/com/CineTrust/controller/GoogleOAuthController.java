package com.CineTrust.controller;




import com.CineTrust.service.GoogleOAuthService;
import com.CineTrust.service.UserSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@RestController
@Tag(name = "Google OAuth Controller", description = "Handles Google OAuth2 authentication and dashboard routes")
public class GoogleOAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthController.class);
    private final GoogleOAuthService oAuthService;
    private final UserSessionService userSessionService;

    public GoogleOAuthController(GoogleOAuthService oAuthService, UserSessionService userSessionService) {
        this.oAuthService = oAuthService;
        this.userSessionService = userSessionService;
    }

    @Operation(summary = "Redirects user to Google OAuth login page")
    @GetMapping("/login")
    public void login(HttpServletResponse resp) throws IOException {
        logger.info("Received request to initiate Google OAuth login flow");
        String url = oAuthService.buildLoginUrl();
        logger.debug("Redirecting to Google OAuth URL: {}", url);
        resp.sendRedirect(url);
    }

    @Operation(summary = "Handles Google OAuth callback and exchanges code for token")
    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> callback(@RequestParam(required = false) String code,
                                      @RequestParam(required = false) String error) {
        logger.info("OAuth2 callback invoked");

        if (error != null) {
            logger.error("Error received from Google OAuth: {}", error);
            return ResponseEntity.badRequest().body("Error from Google: " + error);
        }

        if (code == null) {
            logger.warn("Missing authorization code in callback request");
            return ResponseEntity.badRequest().body("Missing code");
        }

        logger.debug("Authorization code received: {}", code);
        Map<String, Object> response = oAuthService.handleGoogleCallback(code);
        logger.info("OAuth2 token exchange successful for user: {}", response.get("email"));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Protected test endpoint for verifying JWT authentication")
    @GetMapping("/123testing")
    public String test() {
        logger.info("Accessed protected endpoint: /123testing");
        return "Hello";
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "").trim();
        boolean success = userSessionService.invalidateToken(jwt);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token or already logged out"));
        }
    }
}
