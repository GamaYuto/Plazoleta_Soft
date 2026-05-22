package com.plazoleta.mspedidos.dto;

import lombok.Data;

@Data
public class RestauranteDto {
    private Long id;
    private String nombre;
    private String direccion;
    private Long idPropietario;
}
