package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.dto.CreateRestauranteRequest;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestauranteServiceTest {

    private RestauranteService restauranteService;

    @Mock
    private RestauranteRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restauranteService = new RestauranteService(repository);
    }

    @Test
    void shouldCreateRestaurante() {
        CreateRestauranteRequest request = new CreateRestauranteRequest();
        request.setNombre("La Casa");
        request.setDireccion("Calle 123");
        request.setIdPropietario(1L);

        when(repository.save(any(Restaurante.class))).thenAnswer(invocation -> {
            Restaurante restaurante = invocation.getArgument(0);
            restaurante.setId(10L);
            return restaurante;
        });

        var response = restauranteService.createRestaurante(request);

        assertEquals(10L, response.getId());
        assertEquals("La Casa", response.getNombre());
        assertEquals("Calle 123", response.getDireccion());
        assertEquals(1L, response.getIdPropietario());
    }

    @Test
    void shouldListAllRestaurantes() {
        when(repository.findAll()).thenReturn(List.of(
                new Restaurante(1L, "A", "Dir A", 2L),
                new Restaurante(2L, "B", "Dir B", 3L)
        ));

        var restaurantes = restauranteService.listAll();

        assertEquals(2, restaurantes.size());
        assertEquals("A", restaurantes.get(0).getNombre());
    }

    @Test
    void shouldReturnRestauranteById() {
        when(repository.findById(5L)).thenReturn(Optional.of(new Restaurante(5L, "Test", "Dir", 1L)));

        var restaurante = restauranteService.findById(5L);

        assertEquals(5L, restaurante.getId());
        assertEquals("Test", restaurante.getNombre());
    }
}
