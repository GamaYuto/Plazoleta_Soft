package com.plazoleta.mspedidos.controller;

import com.plazoleta.mspedidos.dto.CreatePedidoRequest;
import com.plazoleta.mspedidos.dto.EntregarPedidoRequest;
import com.plazoleta.mspedidos.dto.PedidoResponse;
import com.plazoleta.mspedidos.security.UsuarioPrincipal;
import com.plazoleta.mspedidos.service.PedidoService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CreatePedidoRequest request,
                                                      Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(pedidoService.crearPedido(request, principal.getId()));
    }

    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelarPedido(@PathVariable UUID id,
                                               Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        pedidoService.cancelarPedido(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('EMPLEADO')")
    public List<PedidoResponse> obtenerPendientes(@RequestParam Long restauranteId) {
        return pedidoService.obtenerPedidosPendientes(restauranteId);
    }

    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PedidoResponse> asignarPedido(@PathVariable UUID id,
                                                        @RequestParam Long empleadoId) {
        return ResponseEntity.ok(pedidoService.asignarPedido(id, empleadoId));
    }

    @PatchMapping("/{id}/listo")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PedidoResponse> marcarListo(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.marcarListo(id));
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PedidoResponse> entregarPedido(@PathVariable UUID id,
                                                         @Valid @RequestBody EntregarPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.entregarPedido(id, request.getPin()));
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<PedidoResponse> obtenerMisPedidos(Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return pedidoService.obtenerPedidosCliente(principal.getId());
    }
}
