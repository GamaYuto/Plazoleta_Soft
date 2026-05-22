package com.plazoleta.mstrazabilidad.controller;

import com.plazoleta.mstrazabilidad.model.Evento;
import com.plazoleta.mstrazabilidad.repository.EventoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trazabilidad")
public class EventoController {
    private final EventoRepository repository;

    public EventoController(EventoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/eventos")
    public List<Evento> listar() {
        return repository.findAll();
    }

    @PostMapping("/eventos")
    public ResponseEntity<Evento> crear(@Valid @RequestBody Evento evento) {
        return ResponseEntity.ok(repository.save(evento));
    }
}
