package com.plazoleta.mspedidos.repository;

import com.plazoleta.mspedidos.model.Pedido;
import com.plazoleta.mspedidos.model.PedidoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByIdClienteAndEstadoIn(Long idCliente, List<PedidoEstado> estados);
    List<Pedido> findByIdClienteOrderByFechaCreacionDesc(Long idCliente);
}
