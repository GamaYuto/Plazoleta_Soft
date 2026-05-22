package com.plazoleta.mspedidos.client;

import com.plazoleta.mspedidos.dto.TrazabilidadEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-trazabilidad", url = "${mstrazabilidad.url:}")
public interface FeignMsTrazabilidadClient {

    @PostMapping("/api/trazabilidad/eventos")
    void enviarEvento(@RequestBody TrazabilidadEventDto evento);
}
