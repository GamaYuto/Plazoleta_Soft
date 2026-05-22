package com.plazoleta.mspedidos.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
