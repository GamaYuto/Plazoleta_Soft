package com.plazoleta.msauth.dto;

import com.plazoleta.msauth.model.Role;

public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String correo;
    private Role role;

    public UsuarioResponse(Long id, String nombre, String correo, Role role) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
