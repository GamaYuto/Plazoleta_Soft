package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreateRestauranteRequest;
import com.plazoleta.msrestaurante.dto.RestauranteResponse;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.service.RestauranteService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {
    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public List<RestauranteResponse> listar() {
        return restauranteService.listAll();
    }

    @GetMapping("/{id}")
    public RestauranteResponse obtener(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.findById(id);
        return new RestauranteResponse(restaurante.getId(), restaurante.getNombre(), restaurante.getDireccion(), restaurante.getIdPropietario());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestauranteResponse> crear(@Valid @RequestBody CreateRestauranteRequest request) {
        return ResponseEntity.ok(restauranteService.createRestaurante(request));
    }
}
