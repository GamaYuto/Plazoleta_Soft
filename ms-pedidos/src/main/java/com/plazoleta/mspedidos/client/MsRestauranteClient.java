package com.plazoleta.mspedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class MsRestauranteClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MsRestauranteClient(RestTemplate restTemplate,
                               @Value("${msrestaurante.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<Map<String, Object>> getPlatosByRestaurante(Long restauranteId) {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                baseUrl + "/restaurantes/" + restauranteId + "/platos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }
}
