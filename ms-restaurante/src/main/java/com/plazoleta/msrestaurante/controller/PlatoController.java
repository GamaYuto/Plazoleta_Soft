package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreatePlatoRequest;
import com.plazoleta.msrestaurante.dto.PlatoResponse;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.security.UsuarioPrincipal;
import com.plazoleta.msrestaurante.service.PlatoService;
import com.plazoleta.msrestaurante.service.RestauranteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/platos")
public class PlatoController {

    private final PlatoService platoService;
    private final RestauranteService restauranteService;

    public PlatoController(PlatoService platoService, RestauranteService restauranteService) {
        this.platoService = platoService;
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public List<PlatoResponse> listar(@PathVariable Long restauranteId) {
        return platoService.getPlatosByRestaurante(restauranteId);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<PlatoResponse> crear(@PathVariable Long restauranteId,
                                               @Valid @RequestBody CreatePlatoRequest request,
                                               Authentication authentication) {
        ensureOwner(authentication, restauranteId);
        return ResponseEntity.ok(platoService.createPlato(restauranteId, request));
    }

    @PutMapping("/{platoId}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<PlatoResponse> actualizar(@PathVariable Long restauranteId,
                                                     @PathVariable Long platoId,
                                                     @Valid @RequestBody CreatePlatoRequest request,
                                                     Authentication authentication) {
        ensureOwner(authentication, restauranteId);
        return ResponseEntity.ok(platoService.updatePlato(restauranteId, platoId, request));
    }

    @DeleteMapping("/{platoId}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long restauranteId,
                                         @PathVariable Long platoId,
                                         Authentication authentication) {
        ensureOwner(authentication, restauranteId);
        platoService.deletePlato(restauranteId, platoId);
        return ResponseEntity.noContent().build();
    }

    private void ensureOwner(Authentication authentication, Long restauranteId) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Restaurante restaurante = restauranteService.findById(restauranteId);
        if (!restaurante.getIdPropietario().equals(principal.getId())) {
            throw new SecurityException("Solo el propietario del restaurante puede modificar los platos");
        }
    }
}
