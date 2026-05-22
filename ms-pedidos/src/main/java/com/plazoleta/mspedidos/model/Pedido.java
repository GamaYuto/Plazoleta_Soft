package com.plazoleta.mspedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    private UUID id;

    @NotNull
    private Long idCliente;

    @NotNull
    private Long idRestaurante;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PedidoEstado estado;

    @NotNull
    private Instant fechaCreacion;

    private String pinSeguridad;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PedidoPlato> platos = new HashSet<>();

    public void addPlato(PedidoPlato plato) {
        platos.add(plato);
        plato.setPedido(this);
    }
}
