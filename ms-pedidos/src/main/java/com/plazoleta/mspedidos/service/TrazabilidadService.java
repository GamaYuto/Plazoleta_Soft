package com.plazoleta.mspedidos.service;

import com.plazoleta.mspedidos.client.FeignMsTrazabilidadClient;
import com.plazoleta.mspedidos.dto.TrazabilidadEventDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TrazabilidadService {

    private final FeignMsTrazabilidadClient trazabilidadClient;

    public TrazabilidadService(FeignMsTrazabilidadClient trazabilidadClient) {
        this.trazabilidadClient = trazabilidadClient;
    }

    @Async
    public void enviarEventoCambioEstado(String pedidoId, Long restauranteId, Long clienteId, String estado, String mensaje) {
        TrazabilidadEventDto evento = new TrazabilidadEventDto(pedidoId, restauranteId, clienteId, estado, mensaje);
        trazabilidadClient.enviarEvento(evento);
    }
}
