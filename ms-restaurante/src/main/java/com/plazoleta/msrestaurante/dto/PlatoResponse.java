package com.plazoleta.msrestaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PlatoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
}
