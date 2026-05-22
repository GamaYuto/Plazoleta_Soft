package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.client.MsAuthClient;
import com.plazoleta.msrestaurante.dto.CreateUserRequest;
import com.plazoleta.msrestaurante.model.Empleado;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmpleadoService {

    private final RestauranteService restauranteService;
    private final EmpleadoRepository empleadoRepository;
    private final MsAuthClient msAuthClient;

    public EmpleadoService(RestauranteService restauranteService,
                           EmpleadoRepository empleadoRepository,
                           MsAuthClient msAuthClient) {
        this.restauranteService = restauranteService;
        this.empleadoRepository = empleadoRepository;
        this.msAuthClient = msAuthClient;
    }

    public Map<String, Object> crearPropietario(CreateUserRequest request, String bearerToken) {
        Map<String, Object> payload = buildPayload(request, "PROPIETARIO");
        return msAuthClient.registerUser(payload, bearerToken);
    }

    public Map<String, Object> crearEmpleado(Long restauranteId, CreateUserRequest request, String bearerToken) {
        Restaurante restaurante = restauranteService.findById(restauranteId);
        Map<String, Object> payload = buildPayload(request, "EMPLEADO");
        Map<String, Object> response = msAuthClient.registerUser(payload, bearerToken);

        Object usuarioId = response.get("id");
        if (usuarioId instanceof Number) {
            Empleado empleado = new Empleado();
            empleado.setUsuarioId(((Number) usuarioId).longValue());
            empleado.setRestaurante(restaurante);
            empleadoRepository.save(empleado);
        }
        return response;
    }

    private Map<String, Object> buildPayload(CreateUserRequest request, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", request.getNombre());
        payload.put("correo", request.getCorreo());
        payload.put("password", request.getPassword());
        payload.put("role", role);
        return payload;
    }
}
