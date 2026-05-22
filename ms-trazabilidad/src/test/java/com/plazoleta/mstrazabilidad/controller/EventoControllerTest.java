package com.plazoleta.mstrazabilidad.controller;

import com.plazoleta.mstrazabilidad.model.Evento;
import com.plazoleta.mstrazabilidad.repository.EventoRepository;
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
    private EventoRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new EventoController(repository);
    }

    @Test
    void shouldListEventos() {
        Evento evento = new Evento("1", "PEDIDO", "Pedido creado");
        when(repository.findAll()).thenReturn(List.of(evento));

        List<Evento> eventos = controller.listar();

        assertEquals(1, eventos.size());
        assertEquals("PEDIDO", eventos.get(0).getTipo());
    }

    @Test
    void shouldSaveEvento() {
        Evento evento = new Evento(null, "ENTREGA", "Pedido entregado");
        when(repository.save(evento)).thenReturn(new Evento("1", evento.getTipo(), evento.getDescripcion()));

        ResponseEntity<Evento> response = controller.crear(evento);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
    }
}
