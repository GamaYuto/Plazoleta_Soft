package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreateRestauranteRequest;
import com.plazoleta.msrestaurante.dto.RestauranteResponse;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.service.RestauranteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestauranteControllerTest {

    private RestauranteController controller;

    @Mock
    private RestauranteService restauranteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new RestauranteController(restauranteService);
    }

    @Test
    void shouldListRestaurantes() {
        when(restauranteService.listAll()).thenReturn(List.of(new RestauranteResponse(1L, "A", "Calle", 2L)));

        List<RestauranteResponse> result = controller.listar();

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getNombre());
    }

    @Test
    void shouldReturnRestauranteById() {
        Restaurante restaurante = new Restaurante(1L, "A", "Calle", 2L);
        when(restauranteService.findById(1L)).thenReturn(restaurante);

        RestauranteResponse response = controller.obtener(1L);

        assertEquals(1L, response.getId());
        assertEquals("A", response.getNombre());
    }

    @Test
    void shouldCreateRestaurante() {
        CreateRestauranteRequest request = new CreateRestauranteRequest();
        request.setNombre("A");
        request.setDireccion("Calle");
        request.setIdPropietario(2L);

        RestauranteResponse saved = new RestauranteResponse(1L, "A", "Calle", 2L);
        when(restauranteService.createRestaurante(any(CreateRestauranteRequest.class))).thenReturn(saved);

        ResponseEntity<RestauranteResponse> response = controller.crear(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }
}
