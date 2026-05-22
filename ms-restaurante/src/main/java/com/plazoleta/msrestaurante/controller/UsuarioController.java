package com.plazoleta.msrestaurante.controller;

import com.plazoleta.msrestaurante.dto.CreateUserRequest;
import com.plazoleta.msrestaurante.security.UsuarioPrincipal;
import com.plazoleta.msrestaurante.service.EmpleadoService;
import com.plazoleta.msrestaurante.service.RestauranteService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UsuarioController {

    private final EmpleadoService empleadoService;
    private final RestauranteService restauranteService;

    public UsuarioController(EmpleadoService empleadoService, RestauranteService restauranteService) {
        this.empleadoService = empleadoService;
        this.restauranteService = restauranteService;
    }

    @PostMapping("/propietarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> crearPropietario(@Valid @RequestBody CreateUserRequest request,
                                                                 HttpServletRequest servletRequest) {
        String authHeader = servletRequest.getHeader("Authorization");
        return ResponseEntity.ok(empleadoService.crearPropietario(request, authHeader));
    }

    @PostMapping("/restaurantes/{restauranteId}/empleados")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Map<String, Object>> crearEmpleado(@PathVariable Long restauranteId,
                                                             @Valid @RequestBody CreateUserRequest request,
                                                             Authentication authentication,
                                                             HttpServletRequest servletRequest) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        if (!restauranteService.findById(restauranteId).getIdPropietario().equals(principal.getId())) {
            throw new SecurityException("Solo el propietario puede agregar empleados a este restaurante");
        }
        String authHeader = servletRequest.getHeader("Authorization");
        return ResponseEntity.ok(empleadoService.crearEmpleado(restauranteId, request, authHeader));
    }
}
