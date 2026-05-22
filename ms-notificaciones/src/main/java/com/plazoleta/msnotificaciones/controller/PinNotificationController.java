package com.plazoleta.msnotificaciones.controller;

import com.plazoleta.msnotificaciones.dto.PinNotificationRequest;
import com.plazoleta.msnotificaciones.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificaciones")
public class PinNotificationController {

    private final NotificationService notificationService;

    public PinNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/enviar-pin")
    public ResponseEntity<String> enviarPin(@Valid @RequestBody PinNotificationRequest request) {
        notificationService.sendPinNotification(request.getTelefonoCliente(), request.getPin(), request.getPedidoId());
        return ResponseEntity.ok("PIN enviado correctamente");
    }
}
