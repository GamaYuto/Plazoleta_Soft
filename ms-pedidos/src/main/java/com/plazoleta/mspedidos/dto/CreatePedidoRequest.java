package com.plazoleta.mspedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreatePedidoRequest {
    @NotNull
    private Long restauranteId;

    @NotNull
    @Size(min = 1)
    private Set<Long> listaIdsPlatos;
}
