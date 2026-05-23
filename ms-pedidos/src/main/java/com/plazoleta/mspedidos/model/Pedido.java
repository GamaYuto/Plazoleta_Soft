package com.plazoleta.mspedidos.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@ToString(exclude = "platos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @EqualsAndHashCode.Include
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

    private Instant fechaPreparacion;

    private Instant fechaListo;

    private Instant fechaEntregado;

    private Long empleadoId;

    private String pinSeguridad;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PedidoPlato> platos = new HashSet<>();

    public void addPlato(PedidoPlato plato) {
        platos.add(plato);
        plato.setPedido(this);
    }
}
