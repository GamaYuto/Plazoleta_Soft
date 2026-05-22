package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.service.RestauranteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalRestauranteController {

    private final RestauranteService restauranteService;

    public InternalRestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @GetMapping("/restaurantes/{id}")
    public ResponseEntity<Restaurante> getRestaurante(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.findById(id));
    }
}
