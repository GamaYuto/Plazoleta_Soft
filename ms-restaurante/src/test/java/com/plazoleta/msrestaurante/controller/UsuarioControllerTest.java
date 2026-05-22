package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreateUserRequest;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.model.Role;
import com.plazoleta.msrestaurante.security.UsuarioPrincipal;
import com.plazoleta.msrestaurante.service.EmpleadoService;
import com.plazoleta.msrestaurante.service.RestauranteService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private UsuarioController controller;

    @Mock
    private EmpleadoService empleadoService;

    @Mock
    private RestauranteService restauranteService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UsuarioController(empleadoService, restauranteService);
    }

    @Test
    void shouldCreatePropietario() {
        CreateUserRequest createUser = new CreateUserRequest();
        createUser.setNombre("Propietario");
        createUser.setCorreo("prop@example.com");
        createUser.setPassword("secret");

        when(empleadoService.crearPropietario(any(CreateUserRequest.class), anyString())).thenReturn(Map.of("status", "ok"));
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        ResponseEntity<Map<String, Object>> response = controller.crearPropietario(createUser, request);

        assertEquals("ok", response.getBody().get("status"));
    }

    @Test
    void shouldCreateEmpleadoWhenOwner() {
        CreateUserRequest createUser = new CreateUserRequest();
        createUser.setNombre("Empleado");
        createUser.setCorreo("empleado@example.com");
        createUser.setPassword("secret");

        when(authentication.getPrincipal()).thenReturn(new UsuarioPrincipal(100L, "owner@example.com", Role.PROPIETARIO));
        when(restauranteService.findById(1L)).thenReturn(new Restaurante(1L, "R", "Dir", 100L));
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(empleadoService.crearEmpleado(eq(1L), any(CreateUserRequest.class), anyString())).thenReturn(Map.of("status", "ok"));

        ResponseEntity<Map<String, Object>> response = controller.crearEmpleado(1L, createUser, authentication, request);

        assertEquals("ok", response.getBody().get("status"));
    }
}
