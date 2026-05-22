package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.client.MsAuthClient;
import com.plazoleta.msrestaurante.dto.CreateUserRequest;
import com.plazoleta.msrestaurante.model.Empleado;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmpleadoServiceTest {

    private EmpleadoService empleadoService;

    @Mock
    private RestauranteService restauranteService;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private MsAuthClient msAuthClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        empleadoService = new EmpleadoService(restauranteService, empleadoRepository, msAuthClient);
    }

    @Test
    void shouldCreatePropietarioPayloadAndCallAuth() {
        CreateUserRequest request = new CreateUserRequest();
        request.setNombre("Propietario");
        request.setCorreo("prop@example.com");
        request.setPassword("secret");

        when(msAuthClient.registerUser(any(Map.class), anyString())).thenReturn(Map.of("id", 1, "role", "PROPIETARIO"));

        Map<String, Object> response = empleadoService.crearPropietario(request, "Bearer token");

        assertEquals("PROPIETARIO", response.get("role"));
        verify(msAuthClient, times(1)).registerUser(any(Map.class), anyString());
    }

    @Test
    void shouldCreateEmpleadoAndSaveEmpleadoRecord() {
        CreateUserRequest request = new CreateUserRequest();
        request.setNombre("Empleado");
        request.setCorreo("empleado@example.com");
        request.setPassword("secret");

        Restaurante restaurante = new Restaurante(1L, "R", "Dir", 5L);
        when(restauranteService.findById(1L)).thenReturn(restaurante);
        when(msAuthClient.registerUser(any(Map.class), anyString())).thenReturn(Map.of("id", 2));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> response = empleadoService.crearEmpleado(1L, request, "Bearer token");

        assertEquals(2, response.get("id"));
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }
}
