package com.plazoleta.mspedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrazabilidadEventDto {
    private String pedidoId;
    private Long restauranteId;
    private Long clienteId;
    private String estado;
    private String mensaje;
}
