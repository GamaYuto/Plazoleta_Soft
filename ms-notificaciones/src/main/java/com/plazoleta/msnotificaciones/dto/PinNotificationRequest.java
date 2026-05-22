package com.plazoleta.msnotificaciones.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PinNotificationRequest {
    @NotBlank
    private String telefonoCliente;

    @NotBlank
    private String pin;

    @NotBlank
    private String pedidoId;
}
