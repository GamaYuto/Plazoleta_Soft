package com.plazoleta.msauth.controller;

import com.plazoleta.msauth.dto.AuthResponse;
import com.plazoleta.msauth.dto.LoginRequest;
import com.plazoleta.msauth.dto.RegisterRequest;
import com.plazoleta.msauth.dto.UsuarioResponse;
import com.plazoleta.msauth.security.UsuarioPrincipal;
import com.plazoleta.msauth.service.AuthService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                    Authentication authentication) {
        Optional<UsuarioPrincipal> requester = Optional.empty();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UsuarioPrincipal) {
            requester = Optional.of((UsuarioPrincipal) authentication.getPrincipal());
        }
        return ResponseEntity.ok(authService.registerAndAuthenticate(request, requester));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UsuarioResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }
}
