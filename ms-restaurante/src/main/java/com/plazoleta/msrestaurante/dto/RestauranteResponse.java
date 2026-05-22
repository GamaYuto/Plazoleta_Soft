package com.plazoleta.msrestaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestauranteResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private Long idPropietario;
}
