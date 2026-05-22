package com.plazoleta.mspedidos.repository;

import com.plazoleta.mspedidos.model.PedidoPlato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoPlatoRepository extends JpaRepository<PedidoPlato, Long> {
    List<PedidoPlato> findByPedidoId(UUID pedidoId);
}
