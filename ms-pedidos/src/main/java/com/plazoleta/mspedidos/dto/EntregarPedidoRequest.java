package com.plazoleta.mspedidos.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntregarPedidoRequest {
    @NotBlank
    private String pin;
}
