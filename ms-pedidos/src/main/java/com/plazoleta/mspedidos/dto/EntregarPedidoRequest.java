package com.plazoleta.mspedidos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntregarPedidoRequest {
    @NotBlank
    private String pinSeguridad;
}
