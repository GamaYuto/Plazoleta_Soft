package com.plazoleta.mstrazabilidad.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class UsuarioPrincipal {

    private final Long id;
    private final String correo;
    private final Role role;

    public UsuarioPrincipal(Long id, String correo, Role role) {
        this.id = id;
        this.correo = correo;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public Role getRole() {
        return role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
