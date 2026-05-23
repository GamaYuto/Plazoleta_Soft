package com.plazoleta.mstrazabilidad.service;

import com.plazoleta.mstrazabilidad.model.Evento;
import com.plazoleta.mstrazabilidad.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrazabilidadService {

    private final EventoRepository eventoRepository;

    public TrazabilidadService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarEventosPorPedidoId(String pedidoId) {
        return eventoRepository.findByPedidoId(pedidoId);
    }
}
