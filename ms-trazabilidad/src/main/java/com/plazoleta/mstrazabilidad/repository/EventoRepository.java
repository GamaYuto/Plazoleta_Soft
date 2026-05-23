package com.plazoleta.mstrazabilidad.repository;

import com.plazoleta.mstrazabilidad.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByPedidoId(String pedidoId);
}
