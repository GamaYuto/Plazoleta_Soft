package com.plazoleta.msnotificaciones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final String smsProvider;

    public NotificationService(@Value("${sms.provider:mock}") String smsProvider) {
        this.smsProvider = smsProvider;
    }

    public void sendPinNotification(String telefonoCliente, String pin, String pedidoId) {
        String message = String.format("Pedido %s está listo. Usá el PIN %s para la entrega.", pedidoId, pin);
        if ("twilio" .equalsIgnoreCase(smsProvider) || "sns".equalsIgnoreCase(smsProvider)) {
            log.info("[MOCK SMS] Simulando envío con proveedor {} a {}: {}", smsProvider, telefonoCliente, message);
        } else {
            log.info("[MOCK SMS] Para: {} | Mensaje: {}", telefonoCliente, message);
        }
    }

    public void notifyEmployees(Long restauranteId, String message) {
        log.info("Notificación a empleados del restaurante {}: {}", restauranteId, message);
        // Aquí se podría guardar en BD o empujar por WebSocket en una versión futura.
    }
}
