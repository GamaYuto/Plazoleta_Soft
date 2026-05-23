package com.plazoleta.mstrazabilidad.controller;

import com.plazoleta.mstrazabilidad.model.Evento;
import com.plazoleta.mstrazabilidad.service.TrazabilidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoControllerTest {

    private EventoController controller;

    @Mock
    private TrazabilidadService trazabilidadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new EventoController(trazabilidadService);
    }

    @Test
    void shouldListEventos() {
        Evento evento = new Evento("1", "100", 10L, 20L, "PENDIENTE", "Pedido creado");
        when(trazabilidadService.listarEventos()).thenReturn(List.of(evento));

        List<Evento> eventos = controller.listar();

        assertEquals(1, eventos.size());
        assertEquals("100", eventos.get(0).getPedidoId());
    }

    @Test
    void shouldSaveEvento() {
        Evento evento = new Evento(null, "100", 10L, 20L, "LISTO", "Pedido listo");
        when(trazabilidadService.crearEvento(evento)).thenReturn(new Evento("1", evento.getPedidoId(), evento.getRestauranteId(), evento.getClienteId(), evento.getEstado(), evento.getMensaje()));

        ResponseEntity<Evento> response = controller.crear(evento);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("100", response.getBody().getPedidoId());
    }
}
