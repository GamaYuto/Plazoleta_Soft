package com.plazoleta.msrestaurante.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class MsAuthClient {

    private final RestTemplate restTemplate;
    private final String authBaseUrl;
    private final String adminToken;

    public MsAuthClient(RestTemplate restTemplate,
                        @Value("${msauth.url}") String authBaseUrl,
                        @Value("${msauth.admin.token:}") String adminToken) {
        this.restTemplate = restTemplate;
        this.authBaseUrl = authBaseUrl;
        this.adminToken = adminToken;
    }

    public Map<String, Object> registerUser(Map<String, Object> payload, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        } else if (!adminToken.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(authBaseUrl + "/auth/register", HttpMethod.POST, entity, Map.class).getBody();
    }
}
