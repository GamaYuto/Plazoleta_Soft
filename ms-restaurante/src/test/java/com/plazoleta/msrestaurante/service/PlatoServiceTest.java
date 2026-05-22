package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.dto.CreatePlatoRequest;
import com.plazoleta.msrestaurante.model.Plato;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PlatoServiceTest {

    private PlatoService platoService;

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private RestauranteService restauranteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        platoService = new PlatoService(platoRepository, restauranteService);
    }

    @Test
    void shouldCreatePlato() {
        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 2L);
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        when(platoRepository.save(any(Plato.class))).thenAnswer(invocation -> {
            Plato plato = invocation.getArgument(0);
            plato.setId(100L);
            return plato;
        });

        CreatePlatoRequest request = new CreatePlatoRequest();
        request.setNombre("Ceviche");
        request.setDescripcion("Fresco");
        request.setPrecio(new BigDecimal("12.50"));

        var response = platoService.createPlato(1L, request);

        assertEquals(100L, response.getId());
        assertEquals("Ceviche", response.getNombre());
    }

    @Test
    void shouldUpdatePlato() {
        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 2L);
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        Plato plato = new Plato(5L, "Old", new BigDecimal("5.00"), "Desc", restaurante);
        when(platoRepository.findById(5L)).thenReturn(Optional.of(plato));
        when(platoRepository.save(any(Plato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreatePlatoRequest request = new CreatePlatoRequest();
        request.setNombre("New");
        request.setDescripcion("Updated");
        request.setPrecio(new BigDecimal("8.00"));

        var response = platoService.updatePlato(1L, 5L, request);

        assertEquals("New", response.getNombre());
        assertEquals("Updated", response.getDescripcion());
    }

    @Test
    void shouldDeletePlato() {
        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 2L);
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        Plato plato = new Plato(5L, "Old", new BigDecimal("5.00"), "Desc", restaurante);
        when(platoRepository.findById(5L)).thenReturn(Optional.of(plato));

        platoService.deletePlato(1L, 5L);

        verify(platoRepository, times(1)).deleteById(eq(5L));
    }
}
