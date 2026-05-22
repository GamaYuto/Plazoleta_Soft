package com.plazoleta.msauth.service;

import com.plazoleta.msauth.dto.AuthResponse;
import com.plazoleta.msauth.dto.LoginRequest;
import com.plazoleta.msauth.dto.RegisterRequest;
import com.plazoleta.msauth.dto.UsuarioResponse;
import com.plazoleta.msauth.model.Usuario;
import com.plazoleta.msauth.model.Role;
import com.plazoleta.msauth.repository.UsuarioRepository;
import com.plazoleta.msauth.security.JwtUtils;
import com.plazoleta.msauth.security.UsuarioPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public UsuarioResponse register(RegisterRequest request, Optional<UsuarioPrincipal> requester) {
        if (usuarioRepository.count() == 0) {
            if (request.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("El primer usuario debe ser ADMIN");
            }
        } else {
            UsuarioPrincipal principal = requester
                    .orElseThrow(() -> new SecurityException("Se requiere autenticación ADMIN para crear usuarios"));
            if (principal.getRole() != Role.ADMIN) {
                throw new SecurityException("Solo ADMIN puede crear usuarios");
            }
        }

        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRole(request.getRole());
        Usuario saved = usuarioRepository.save(usuario);
        return new UsuarioResponse(saved.getId(), saved.getNombre(), saved.getCorreo(), saved.getRole());
    }

    public AuthResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        return new AuthResponse(jwt, request.getCorreo());
    }

    public UsuarioResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new SecurityException("Usuario no autenticado");
        }
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return new UsuarioResponse(principal.getId(), principal.getNombre(), principal.getUsername(), principal.getRole());
    }

    public UsuarioResponse getUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRole());
    }
}
