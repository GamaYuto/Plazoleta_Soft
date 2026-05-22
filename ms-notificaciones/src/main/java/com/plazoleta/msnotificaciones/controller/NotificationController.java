package com.plazoleta.msnotificaciones.controller;

import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificationController {

    @PostMapping("/sms")
    public ResponseEntity<String> enviarSms(@RequestParam @NotBlank String destino,
                                            @RequestParam @NotBlank String mensaje) {
        // Integración con Twilio / AWS SNS pendiente de configurar
        return ResponseEntity.ok("Notificación enviada a " + destino + " (simulado)");
    }
}
