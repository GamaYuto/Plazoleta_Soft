package com.plazoleta.msrestaurante.service;

import com.plazoleta.msrestaurante.dto.CreatePlatoRequest;
import com.plazoleta.msrestaurante.dto.PlatoResponse;
import com.plazoleta.msrestaurante.model.Plato;
import com.plazoleta.msrestaurante.model.Restaurante;
import com.plazoleta.msrestaurante.repository.PlatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlatoService {

    private final PlatoRepository repository;
    private final RestauranteService restauranteService;

    public PlatoService(PlatoRepository repository, RestauranteService restauranteService) {
        this.repository = repository;
        this.restauranteService = restauranteService;
    }

    public PlatoResponse createPlato(Long restauranteId, CreatePlatoRequest request) {
        Restaurante restaurante = restauranteService.findById(restauranteId);
        Plato plato = new Plato();
        plato.setNombre(request.getNombre());
        plato.setDescripcion(request.getDescripcion());
        plato.setPrecio(request.getPrecio());
        plato.setRestaurante(restaurante);
        Plato saved = repository.save(plato);
        return toResponse(saved);
    }

    public PlatoResponse updatePlato(Long restauranteId, Long platoId, CreatePlatoRequest request) {
        verifyOwnership(restauranteId, platoId);
        Plato plato = repository.findById(platoId).orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        plato.setNombre(request.getNombre());
        plato.setDescripcion(request.getDescripcion());
        plato.setPrecio(request.getPrecio());
        return toResponse(repository.save(plato));
    }

    public void deletePlato(Long restauranteId, Long platoId) {
        verifyOwnership(restauranteId, platoId);
        repository.deleteById(platoId);
    }

    public List<PlatoResponse> getPlatosByRestaurante(Long restauranteId) {
        return repository.findByRestauranteId(restauranteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void verifyOwnership(Long restauranteId, Long platoId) {
        Plato plato = repository.findById(platoId).orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        if (!plato.getRestaurante().getId().equals(restauranteId)) {
            throw new IllegalArgumentException("El plato no pertenece al restaurante especificado");
        }
    }

    private PlatoResponse toResponse(Plato plato) {
        return new PlatoResponse(plato.getId(), plato.getNombre(), plato.getDescripcion(), plato.getPrecio());
    }
}
