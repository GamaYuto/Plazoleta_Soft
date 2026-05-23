package com.plazoleta.mspedidos.service;

import com.plazoleta.mspedidos.client.FeignMsAuthClient;
import com.plazoleta.mspedidos.client.FeignMsRestauranteClient;
import com.plazoleta.mspedidos.client.MsNotificacionesClient;
import com.plazoleta.mspedidos.dto.CreatePedidoRequest;
import com.plazoleta.mspedidos.dto.EntregarPedidoRequest;
import com.plazoleta.mspedidos.dto.NotificacionPinRequest;
import com.plazoleta.mspedidos.dto.PedidoResponse;
import com.plazoleta.mspedidos.dto.PlatoDto;
import com.plazoleta.mspedidos.dto.RestauranteDto;
import com.plazoleta.mspedidos.dto.UsuarioDto;
import lombok.extern.slf4j.Slf4j;
import com.plazoleta.mspedidos.model.Pedido;
import com.plazoleta.mspedidos.model.PedidoEstado;
import com.plazoleta.mspedidos.model.PedidoPlato;
import com.plazoleta.mspedidos.repository.PedidoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final FeignMsRestauranteClient msRestauranteClient;
    private final FeignMsAuthClient msAuthClient;
    private final TrazabilidadService trazabilidadService;
    private final MsNotificacionesClient msNotificacionesClient;
    private final StringRedisTemplate redisTemplate;

    public PedidoService(PedidoRepository pedidoRepository,
                         FeignMsRestauranteClient msRestauranteClient,
                         FeignMsAuthClient msAuthClient,
                         TrazabilidadService trazabilidadService,
                         MsNotificacionesClient msNotificacionesClient,
                         StringRedisTemplate redisTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.msRestauranteClient = msRestauranteClient;
        this.msAuthClient = msAuthClient;
        this.trazabilidadService = trazabilidadService;
        this.msNotificacionesClient = msNotificacionesClient;
        this.redisTemplate = redisTemplate;
    }

    public PedidoResponse crearPedido(CreatePedidoRequest request, Long clienteId) {
        UsuarioDto cliente = msAuthClient.getUsuarioById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        validarPedidoActivo(clienteId);
        RestauranteDto restaurante = msRestauranteClient.getRestaurante(request.getRestauranteId());
        if (restaurante == null) {
            throw new IllegalArgumentException("Restaurante no encontrado");
        }
        validarPlatosEnRestaurante(restaurante.getId(), request.getListaIdsPlatos());

        Pedido pedido = new Pedido();
        pedido.setId(UUID.randomUUID());
        pedido.setIdCliente(clienteId);
        pedido.setIdRestaurante(request.getRestauranteId());
        pedido.setEstado(PedidoEstado.PENDIENTE);
        pedido.setFechaCreacion(Instant.now());

        for (Long platoId : request.getListaIdsPlatos()) {
            PedidoPlato pedidoPlato = new PedidoPlato();
            pedidoPlato.setPlatoId(platoId);
            pedido.addPlato(pedidoPlato);
        }

        Pedido saved = pedidoRepository.save(pedido);
        encolarPedido(saved);
        trazabilidadService.enviarEventoCambioEstado(saved.getId().toString(), saved.getIdRestaurante(), saved.getIdCliente(), saved.getEstado().name(), "Pedido creado");
        return toResponse(saved);
    }

    private void validarPedidoActivo(Long clienteId) {
        List<PedidoEstado> activos = List.of(PedidoEstado.PENDIENTE, PedidoEstado.EN_PREPARACION, PedidoEstado.LISTO);
        if (!pedidoRepository.findByIdClienteAndEstadoIn(clienteId, activos).isEmpty()) {
            throw new IllegalStateException("El cliente ya tiene un pedido activo");
        }
    }

    private void validarPlatosEnRestaurante(Long restauranteId, Set<Long> listaIdsPlatos) {
        List<PlatoDto> platos = msRestauranteClient.getPlatosByRestaurante(restauranteId);
        if (platos == null || platos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron platos para el restaurante");
        }
        Set<Long> idsValidos = platos.stream()
                .map(PlatoDto::getId)
                .collect(Collectors.toSet());
        if (!idsValidos.containsAll(listaIdsPlatos)) {
            throw new IllegalArgumentException("Algunos platos no pertenecen al restaurante especificado");
        }
    }

    public void cancelarPedido(UUID pedidoId, Long clienteId) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (!pedido.getIdCliente().equals(clienteId)) {
            throw new SecurityException("El cliente no es dueño del pedido");
        }
        if (pedido.getEstado() != PedidoEstado.PENDIENTE) {
            throw new IllegalStateException("Solo pedidos en estado PENDIENTE pueden ser cancelados");
        }
        pedido.setEstado(PedidoEstado.CANCELADO);
        pedidoRepository.save(pedido);
        removerPedidoDeCola(pedidoId, pedido.getIdRestaurante());
        trazabilidadService.enviarEventoCambioEstado(pedido.getId().toString(), pedido.getIdRestaurante(), pedido.getIdCliente(), pedido.getEstado().name(), "Pedido cancelado");
    }

    public List<PedidoResponse> obtenerPedidosPendientes(Long restauranteId) {
        String key = colaPendientesKey(restauranteId);
        List<String> valores = redisTemplate.opsForList().range(key, 0, -1);
        if (valores == null || valores.isEmpty()) {
            return Collections.emptyList();
        }
        return valores.stream()
                .map(UUID::fromString)
                .map(this::obtenerPedido)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PedidoResponse asignarPedido(UUID pedidoId, Long empleadoId) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (pedido.getEstado() != PedidoEstado.PENDIENTE) {
            throw new IllegalStateException("Solo pedidos PENDIENTE pueden ser asignados");
        }
        pedido.setEstado(PedidoEstado.EN_PREPARACION);
        pedido.setEmpleadoId(empleadoId);
        pedido.setFechaPreparacion(Instant.now());
        Pedido saved = pedidoRepository.save(pedido);
        removerPedidoDeCola(pedidoId, pedido.getIdRestaurante());
        trazabilidadService.enviarEventoCambioEstado(saved.getId().toString(), saved.getIdRestaurante(), saved.getIdCliente(), saved.getEstado().name(), "Pedido asignado a preparación");
        return toResponse(saved);
    }

    public PedidoResponse marcarListo(UUID pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (pedido.getEstado() != PedidoEstado.EN_PREPARACION) {
            throw new IllegalStateException("Solo pedidos EN_PREPARACION pueden marcarse como LISTO");
        }
        pedido.setEstado(PedidoEstado.LISTO);
        pedido.setFechaListo(Instant.now());
        pedido.setPinSeguridad(generarPin());
        Pedido saved = pedidoRepository.save(pedido);
        enviarPinNotificationAsync(saved);
        trazabilidadService.enviarEventoCambioEstado(saved.getId().toString(), saved.getIdRestaurante(), saved.getIdCliente(), saved.getEstado().name(), "Pedido listo para entrega");
        return toResponse(saved);
    }

    public PedidoResponse entregarPedido(UUID pedidoId, String pin) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (pedido.getEstado() != PedidoEstado.LISTO) {
            throw new IllegalStateException("Solo pedidos LISTO pueden entregarse");
        }
        if (!pin.equals(pedido.getPinSeguridad())) {
            throw new SecurityException("PIN de seguridad inválido");
        }
        pedido.setEstado(PedidoEstado.ENTREGADO);
        pedido.setFechaEntregado(Instant.now());
        Pedido saved = pedidoRepository.save(pedido);
        trazabilidadService.enviarEventoCambioEstado(saved.getId().toString(), saved.getIdRestaurante(), saved.getIdCliente(), saved.getEstado().name(), "Pedido entregado");
        return toResponse(saved);
    }

    public List<PedidoResponse> obtenerPedidosCliente(Long clienteId) {
        return pedidoRepository.findByIdClienteOrderByFechaCreacionDesc(clienteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Pedido obtenerPedido(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    private void encolarPedido(Pedido pedido) {
        String key = colaPendientesKey(pedido.getIdRestaurante());
        redisTemplate.opsForList().rightPush(key, pedido.getId().toString());
    }

    private void removerPedidoDeCola(UUID pedidoId, Long restauranteId) {
        String key = colaPendientesKey(restauranteId);
        redisTemplate.opsForList().remove(key, 0, pedidoId.toString());
    }

    private String colaPendientesKey(Long restauranteId) {
        return "pedidos:pendientes:" + restauranteId;
    }

    private String generarPin() {
        int numero = new Random().nextInt(900000) + 100000;
        return String.valueOf(numero);
    }

    private void enviarPinNotificationAsync(Pedido pedido) {
        CompletableFuture.runAsync(() -> {
            log.info("PIN generado para pedido {}: {}", pedido.getId(), pedido.getPinSeguridad());

            NotificacionPinRequest request = new NotificacionPinRequest();
            request.setPedidoId(pedido.getId().toString());
            request.setPin(pedido.getPinSeguridad());
            request.setTelefonoCliente("" );

            // No hay teléfono de cliente disponible en el modelo actual.
            // Por ahora sólo logueamos el PIN y dejamos preparada la llamada.
            if (request.getTelefonoCliente() == null || request.getTelefonoCliente().isBlank()) {
                log.info("No se envió SMS porque no hay teléfono de cliente disponible para pedido {}", pedido.getId());
                return;
            }

            try {
                msNotificacionesClient.enviarPinNotification(request);
            } catch (Exception e) {
                log.error("Error al notificar PIN: {}", e.getMessage());
            }
        });
    }

    private PedidoResponse toResponse(Pedido pedido) {
        Set<Long> idsPlatos = pedido.getPlatos().stream()
                .map(PedidoPlato::getPlatoId)
                .collect(Collectors.toSet());
        return new PedidoResponse(
                pedido.getId(),
                pedido.getIdCliente(),
                pedido.getIdRestaurante(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaPreparacion(),
                pedido.getFechaListo(),
                pedido.getFechaEntregado(),
                pedido.getEmpleadoId(),
                pedido.getPinSeguridad(),
                idsPlatos
        );
    }
}
