package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreatePlatoRequest;
import com.plazoleta.msrestaurante.dto.PlatoResponse;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.model.Role;
import com.plazoleta.msrestaurante.security.UsuarioPrincipal;
import com.plazoleta.msrestaurante.service.PlatoService;
import com.plazoleta.msrestaurante.service.RestauranteService;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlatoControllerTest {

    private PlatoController controller;

    @Mock
    private PlatoService platoService;

    @Mock
    private RestauranteService restauranteService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PlatoController(platoService, restauranteService);
    }

    @Test
    void shouldListPlatosByRestaurante() {
        when(platoService.getPlatosByRestaurante(1L)).thenReturn(List.of(new PlatoResponse(1L, "Ceviche", "Fresco", BigDecimal.TEN)));

        List<PlatoResponse> result = controller.listar(1L);

        assertEquals(1, result.size());
        assertEquals("Ceviche", result.get(0).getNombre());
    }

    @Test
    void shouldCreatePlatoWhenOwner() {
        CreatePlatoRequest request = new CreatePlatoRequest();
        request.setNombre("Ceviche");
        request.setDescripcion("Fresco");
        request.setPrecio(BigDecimal.TEN);

        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 100L);
        when(authentication.getPrincipal()).thenReturn(new UsuarioPrincipal(100L, "owner@example.com", Role.PROPIETARIO));
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        when(platoService.createPlato(1L, request)).thenReturn(new PlatoResponse(1L, "Ceviche", "Fresco", BigDecimal.TEN));

        ResponseEntity<PlatoResponse> response = controller.crear(1L, request, authentication);

        assertEquals("Ceviche", response.getBody().getNombre());
    }

    @Test
    void shouldUpdatePlatoWhenOwner() {
        CreatePlatoRequest request = new CreatePlatoRequest();
        request.setNombre("Ceviche Nuevo");
        request.setDescripcion("Fresco");
        request.setPrecio(BigDecimal.TEN);

        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 100L);
        when(authentication.getPrincipal()).thenReturn(new UsuarioPrincipal(100L, "owner@example.com", Role.PROPIETARIO));
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        when(platoService.updatePlato(1L, 1L, request)).thenReturn(new PlatoResponse(1L, "Ceviche Nuevo", "Fresco", BigDecimal.TEN));

        ResponseEntity<PlatoResponse> response = controller.actualizar(1L, 1L, request, authentication);

        assertEquals("Ceviche Nuevo", response.getBody().getNombre());
    }

    @Test
    void shouldDeletePlatoWhenOwner() {
        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 100L);
        when(authentication.getPrincipal()).thenReturn(new UsuarioPrincipal(100L, "owner@example.com", Role.PROPIETARIO));
        when(restauranteService.findById(1L)).thenReturn(restaurante);

        assertDoesNotThrow(() -> controller.eliminar(1L, 1L, authentication));
        verify(platoService, times(1)).deletePlato(1L, 1L);
    }
}
