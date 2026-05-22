package com.plazoleta.msrestaurante.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
