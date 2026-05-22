package com.plazoleta.mspedidos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlatoDto {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
}
