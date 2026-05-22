package com.plazoleta.mspedidos.client;

import com.plazoleta.mspedidos.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-auth", url = "${msauth.url:}")
public interface FeignMsAuthClient {

    @GetMapping("/auth/users/{id}")
    UsuarioDto getUsuarioById(@PathVariable("id") Long id);
}
