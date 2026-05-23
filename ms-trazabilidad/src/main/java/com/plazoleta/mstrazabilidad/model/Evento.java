package com.plazoleta.mstrazabilidad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document(collection = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    @Id
    private String id;

    @NotBlank
    private String pedidoId;

    @NotNull
    private Long restauranteId;

    @NotNull
    private Long clienteId;

    @NotBlank
    private String estado;

    @NotBlank
    private String mensaje;
}
