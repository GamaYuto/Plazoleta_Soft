package com.plazoleta.msrestaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRestauranteRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    @NotNull
    private Long idPropietario;
}
