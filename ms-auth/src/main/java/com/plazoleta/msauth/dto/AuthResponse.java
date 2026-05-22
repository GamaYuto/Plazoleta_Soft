package com.plazoleta.msauth.dto;

public class AuthResponse {
    private String token;
    private String correo;

    public AuthResponse(String token, String correo) {
        this.token = token;
        this.correo = correo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
