package com.plazoleta.mstrazabilidad.controller;

import com.plazoleta.mstrazabilidad.model.Evento;
import com.plazoleta.mstrazabilidad.service.TrazabilidadService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trazabilidad")
public class EventoController {
    private final TrazabilidadService trazabilidadService;

    public EventoController(TrazabilidadService trazabilidadService) {
        this.trazabilidadService = trazabilidadService;
    }

    @GetMapping("/eventos")
    public List<Evento> listar() {
        return trazabilidadService.listarEventos();
    }

    @GetMapping("/eventos/pedidos/{pedidoId}")
    public List<Evento> listarPorPedidoId(@PathVariable String pedidoId) {
        return trazabilidadService.listarEventosPorPedidoId(pedidoId);
    }

    @PostMapping("/eventos")
    public ResponseEntity<Evento> crear(@Valid @RequestBody Evento evento) {
        return ResponseEntity.ok(trazabilidadService.crearEvento(evento));
    }
}
