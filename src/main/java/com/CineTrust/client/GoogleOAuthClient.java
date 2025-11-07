package com.CineTrust.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.annotation.Timed;

import java.util.Map;

@Component
public class GoogleOAuthClient {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthClient.class);

    private final WebClient webClient;

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.redirectUri}")
    private String redirectUri;

    public GoogleOAuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Exchange authorization code for access token.
     * Returns a map containing access_token, id_token, and refresh_token (if provided).
     */
    @Timed(value = "oauth.exchangeCodeForToken", description = "Time taken to exchange auth code for token")
    public Map<String, Object> exchangeCodeForToken(String code) {
        log.info("Exchanging authorization code for access token with Google...");

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        try {
            Map<String, Object> response = webClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Token exchange response: {}", response);
            log.info("Successfully obtained access token from Google for clientId={}", clientId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Google token exchange failed: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during Google token exchange", e);
            throw e;
        }
    }

    /**
     * Fetch user info from Google using the provided access token.
     */
    @Timed(value = "oauth.fetchUserInfo", description = "Time taken to fetch user info from Google")
    public Map<String, Object> fetchUserInfo(String accessToken) {
        log.info("Fetching Google user info using access token...");

        // Handle null or empty access token case
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("Access token is null or empty. Cannot fetch user info.");
            throw new IllegalArgumentException("Access token must not be null or empty");
        }

        try {
            Map<String, Object> userInfo = webClient.get()
                    .uri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Google user info response: {}", userInfo);

            if (userInfo == null || userInfo.isEmpty()) {
                log.warn("No user info returned from Google API.");
                throw new IllegalStateException("Failed to fetch user info from Google");
            }

            log.info("Successfully fetched user info: {}", userInfo.get("email"));
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("Failed to fetch user info from Google: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during fetching user info", e);
            throw e;
        }
    }
}