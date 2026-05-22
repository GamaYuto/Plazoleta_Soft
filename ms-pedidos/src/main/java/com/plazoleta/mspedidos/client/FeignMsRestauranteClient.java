package com.plazoleta.mspedidos.client;

import com.plazoleta.mspedidos.dto.PlatoDto;
import com.plazoleta.mspedidos.dto.RestauranteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-restaurante", url = "${msrestaurante.url:}")
public interface FeignMsRestauranteClient {

    @GetMapping("/internal/restaurantes/{id}")
    RestauranteDto getRestaurante(@PathVariable("id") Long id);

    @GetMapping("/restaurantes/{id}/platos")
    List<PlatoDto> getPlatosByRestaurante(@PathVariable("id") Long id);
}
