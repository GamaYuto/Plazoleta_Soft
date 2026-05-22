package com.plazoleta.mspedidos.dto;

import lombok.Data;

@Data
public class UsuarioDto {
    private Long id;
    private String correo;
    private String nombre;
    private String role;
}
