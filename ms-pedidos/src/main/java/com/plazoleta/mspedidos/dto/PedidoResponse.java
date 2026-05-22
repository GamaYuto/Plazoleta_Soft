package com.plazoleta.mspedidos.dto;

import com.plazoleta.mspedidos.model.PedidoEstado;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PedidoResponse {
    private UUID id;
    private Long idCliente;
    private Long idRestaurante;
    private PedidoEstado estado;
    private Instant fechaCreacion;
    private String pinSeguridad;
    private Set<Long> listaIdsPlatos;
}
