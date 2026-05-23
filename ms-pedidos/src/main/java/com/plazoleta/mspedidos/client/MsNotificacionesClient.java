package com.plazoleta.mspedidos.client;

import com.plazoleta.mspedidos.dto.NotificacionPinRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MsNotificacionesClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MsNotificacionesClient(RestTemplate restTemplate,
                                   @Value("${ms.notificaciones.url:${msnotificaciones.url:http://ms-notificaciones:8080}}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void enviarNotificacion(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(baseUrl + "/api/notificaciones/sms", entity, Void.class);
    }

    public void enviarPinNotification(NotificacionPinRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificacionPinRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForEntity(baseUrl + "/notificaciones/enviar-pin", entity, Void.class);
    }
}
