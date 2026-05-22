package com.plazoleta.msnotificaciones.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationServiceTest {

    @Test
    void shouldLogMockSmsWhenTwilioNotConfigured() {
        NotificationService service = new NotificationService("");

        assertDoesNotThrow(() -> service.sendPinNotification("+1234567890", "123456", "pedido-1"));
    }
}
