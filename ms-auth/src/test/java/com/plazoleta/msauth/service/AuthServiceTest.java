package com.plazoleta.msauth.service;

import com.plazoleta.msauth.dto.AuthResponse;
import com.plazoleta.msauth.dto.LoginRequest;
import com.plazoleta.msauth.dto.RegisterRequest;
import com.plazoleta.msauth.model.Role;
import com.plazoleta.msauth.model.Usuario;
import com.plazoleta.msauth.repository.UsuarioRepository;
import com.plazoleta.msauth.security.JwtUtils;
import com.plazoleta.msauth.security.UsuarioPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(usuarioRepository, passwordEncoder, authenticationManager, jwtUtils);
    }

    @AfterEach
    void cleanup() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRegisterInitialAdminWhenNoUsersExist() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Admin");
        request.setCorreo("admin@example.com");
        request.setPassword("secret");
        request.setRole(Role.ADMIN);

        when(usuarioRepository.count()).thenReturn(0L);
        when(usuarioRepository.findByCorreo(request.getCorreo())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario usuario = i.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        var response = authService.register(request, Optional.empty());

        assertEquals(1L, response.getId());
        assertEquals("admin@example.com", response.getCorreo());
        assertEquals(Role.ADMIN, response.getRole());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void shouldRejectFirstNonAdminRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("User");
        request.setCorreo("user@example.com");
        request.setPassword("secret");
        request.setRole(Role.CLIENTE);

        when(usuarioRepository.count()).thenReturn(0L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request, Optional.empty()));
        assertTrue(exception.getMessage().contains("primer usuario"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void shouldRegisterUserWhenRequesterIsAdmin() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Propietario");
        request.setCorreo("prop@example.com");
        request.setPassword("secret");
        request.setRole(Role.PROPIETARIO);

        UsuarioPrincipal adminPrincipal = UsuarioPrincipal.build(new Usuario(1L, "Admin", "admin@example.com", "encoded", Role.ADMIN));
        when(usuarioRepository.count()).thenReturn(1L);
        when(usuarioRepository.findByCorreo(request.getCorreo())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario usuario = i.getArgument(0);
            usuario.setId(2L);
            return usuario;
        });

        var response = authService.register(request, Optional.of(adminPrincipal));

        assertEquals(2L, response.getId());
        assertEquals("prop@example.com", response.getCorreo());
        assertEquals(Role.PROPIETARIO, response.getRole());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void shouldAuthenticateAndReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setCorreo("admin@example.com");
        request.setPassword("secret");

        UsuarioPrincipal principal = UsuarioPrincipal.build(new Usuario(1L, "Admin", "admin@example.com", "encoded", Role.ADMIN));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        AuthResponse response = authService.authenticate(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("admin@example.com", response.getCorreo());
    }
}
