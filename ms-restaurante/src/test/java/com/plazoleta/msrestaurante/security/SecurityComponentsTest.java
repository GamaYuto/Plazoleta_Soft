package com.plazoleta.msrestaurante.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SecurityComponentsTest {

    @Test
    void shouldCreateJwtAuthenticationFilterBean() {
        JwtUtils jwtUtils = new JwtUtils();
        SecurityConfig securityConfig = new SecurityConfig(jwtUtils);

        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();

        assertNotNull(filter);
    }

    @Test
    void shouldAuthenticateRequestWithValidToken() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, "secret-key-very-long-change-me-1234567890123456");

        String token = Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .claim("id", 1L)
                .claim("role", "PROPIETARIO")
                .signWith(Keys.hmacShaKeyFor("secret-key-very-long-change-me-1234567890123456".getBytes(StandardCharsets.UTF_8)))
                .compact();

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtils);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("user@example.com", authentication.getName());
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
