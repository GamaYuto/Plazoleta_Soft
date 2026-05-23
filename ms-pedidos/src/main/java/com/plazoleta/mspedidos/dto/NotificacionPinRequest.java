package com.plazoleta.mspedidos.dto;

import lombok.Data;

@Data
public class NotificacionPinRequest {
    private String telefonoCliente;
    private String pin;
    private String pedidoId;
}
