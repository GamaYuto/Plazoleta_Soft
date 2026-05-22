package com.plazoleta.msauth.controller;

import com.plazoleta.msauth.dto.UsuarioResponse;
import com.plazoleta.msauth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalUsuarioController {

    private final AuthService authService;

    public InternalUsuarioController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }
}
