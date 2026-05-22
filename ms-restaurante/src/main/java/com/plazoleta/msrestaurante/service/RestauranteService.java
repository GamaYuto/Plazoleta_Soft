package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.dto.CreateRestauranteRequest;
import com.plazoleta.msrestaurante.dto.RestauranteResponse;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    private final RestauranteRepository repository;

    public RestauranteService(RestauranteRepository repository) {
        this.repository = repository;
    }

    public RestauranteResponse createRestaurante(CreateRestauranteRequest request) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(request.getNombre());
        restaurante.setDireccion(request.getDireccion());
        restaurante.setIdPropietario(request.getIdPropietario());
        Restaurante saved = repository.save(restaurante);
        return new RestauranteResponse(saved.getId(), saved.getNombre(), saved.getDireccion(), saved.getIdPropietario());
    }

    public List<RestauranteResponse> listAll() {
        return repository.findAll().stream()
                .map(r -> new RestauranteResponse(r.getId(), r.getNombre(), r.getDireccion(), r.getIdPropietario()))
                .collect(Collectors.toList());
    }

    public Restaurante findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Restaurante no encontrado"));
    }
}
