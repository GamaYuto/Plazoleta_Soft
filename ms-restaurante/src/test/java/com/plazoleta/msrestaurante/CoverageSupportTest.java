package com.plazoleta.msrestaurante;

import com.plazoleta.msrestaurante.dto.CreatePlatoRequest;
import com.plazoleta.msrestaurante.dto.CreateRestauranteRequest;
import com.plazoleta.msrestaurante.dto.CreateUserRequest;
import com.plazoleta.msrestaurante.dto.PlatoResponse;
import com.plazoleta.msrestaurante.dto.RestauranteResponse;
import com.plazoleta.msrestaurante.model.Empleado;
import com.plazoleta.msrestaurante.model.Plato;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.model.Role;
import com.plazoleta.msrestaurante.security.JwtUtils;
import com.plazoleta.msrestaurante.security.UsuarioPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CoverageSupportTest {

    @Test
    void shouldUseModelAndDtoAccessors() {
        CreatePlatoRequest platoRequest = new CreatePlatoRequest();
        platoRequest.setNombre("Ceviche");
        platoRequest.setDescripcion("Fresco");
        platoRequest.setPrecio(java.math.BigDecimal.valueOf(20));
        assertEquals("Ceviche", platoRequest.getNombre());

        CreateRestauranteRequest restauranteRequest = new CreateRestauranteRequest();
        restauranteRequest.setNombre("La Plaza");
        restauranteRequest.setDireccion("Calle");
        restauranteRequest.setIdPropietario(1L);
        assertEquals("La Plaza", restauranteRequest.getNombre());

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setNombre("Juan");
        userRequest.setCorreo("juan@example.com");
        userRequest.setPassword("secret");
        assertEquals("Juan", userRequest.getNombre());

        PlatoResponse platoResponse = new PlatoResponse(1L, "Ceviche", "Fresco", java.math.BigDecimal.valueOf(20));
        assertEquals("Fresco", platoResponse.getDescripcion());

        RestauranteResponse restauranteResponse = new RestauranteResponse(1L, "La Plaza", "Calle", 1L);
        assertEquals(1L, restauranteResponse.getIdPropietario());

        Restaurante restaurante = new Restaurante(1L, "La Plaza", "Calle", 1L);
        assertEquals(1L, restaurante.getIdPropietario());

        Plato plato = new Plato(1L, "Ceviche", java.math.BigDecimal.valueOf(20), "Fresco", restaurante);
        assertEquals("Ceviche", plato.getNombre());

        Empleado empleado = new Empleado();
        empleado.setUsuarioId(2L);
        empleado.setRestaurante(restaurante);
        assertEquals(2L, empleado.getUsuarioId());

        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    void shouldGenerateAndValidateJwtToken() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, "secret-key-very-long-change-me-1234567890123456");

        String token = Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .claim("id", 1L)
                .claim("role", "ADMIN")
                .signWith(Keys.hmacShaKeyFor("secret-key-very-long-change-me-1234567890123456".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("user@example.com", jwtUtils.getUserNameFromJwtToken(token));
        assertEquals("ADMIN", jwtUtils.getRoleFromJwtToken(token));
        assertEquals(1L, jwtUtils.getUserIdFromJwtToken(token));
    }

    @Test
    void shouldVerifyUsuarioPrincipalEqualsAndHashCode() {
        UsuarioPrincipal first = new UsuarioPrincipal(1L, "user@example.com", Role.PROPIETARIO);
        UsuarioPrincipal second = new UsuarioPrincipal(1L, "user@example.com", Role.PROPIETARIO);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PROPIETARIO")));
    }
}
