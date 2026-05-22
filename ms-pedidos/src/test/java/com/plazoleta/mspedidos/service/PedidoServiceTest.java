package com.plazoleta.mspedidos.service;

import com.plazoleta.mspedidos.client.FeignMsAuthClient;
import com.plazoleta.mspedidos.client.FeignMsRestauranteClient;
import com.plazoleta.mspedidos.client.MsNotificacionesClient;
import com.plazoleta.mspedidos.dto.CreatePedidoRequest;
import com.plazoleta.mspedidos.dto.EntregarPedidoRequest;
import com.plazoleta.mspedidos.dto.RestauranteDto;
import com.plazoleta.mspedidos.dto.UsuarioDto;
import com.plazoleta.mspedidos.model.Pedido;
import com.plazoleta.mspedidos.model.PedidoEstado;
import com.plazoleta.mspedidos.repository.PedidoPlatoRepository;
import com.plazoleta.mspedidos.repository.PedidoRepository;
import com.plazoleta.mspedidos.service.TrazabilidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoPlatoRepository pedidoPlatoRepository;

    @Mock
    private FeignMsRestauranteClient msRestauranteClient;

    @Mock
    private FeignMsAuthClient msAuthClient;

    @Mock
    private TrazabilidadService trazabilidadService;

    @Mock
    private MsNotificacionesClient msNotificacionesClient;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        UsuarioDto mockUsuario = new UsuarioDto();
        mockUsuario.setId(5L);
        mockUsuario.setCorreo("cliente@example.com");
        mockUsuario.setNombre("Cliente");
        mockUsuario.setRole("CLIENTE");
        when(msAuthClient.getUsuarioById(anyLong())).thenReturn(mockUsuario);
        pedidoService = new PedidoService(pedidoRepository, msRestauranteClient, msAuthClient, trazabilidadService, msNotificacionesClient, redisTemplate);
    }

    @Test
    void shouldCreatePedidoWhenClienteNoActivoAndPlatosValidos() {
        CreatePedidoRequest request = new CreatePedidoRequest();
        request.setRestauranteId(1L);
        request.setListaIdsPlatos(Set.of(10L, 20L));

        when(pedidoRepository.findByIdClienteAndEstadoIn(anyLong(), anyList())).thenReturn(List.of());
        RestauranteDto restaurant = new RestauranteDto();
        restaurant.setId(1L);
        restaurant.setNombre("Restaurante Test");
        restaurant.setDireccion("Calle Test");
        restaurant.setIdPropietario(2L);
        when(msRestauranteClient.getRestaurante(1L)).thenReturn(restaurant);
        var plato1 = new com.plazoleta.mspedidos.dto.PlatoDto();
        plato1.setId(10L);
        var plato2 = new com.plazoleta.mspedidos.dto.PlatoDto();
        plato2.setId(20L);
        when(msRestauranteClient.getPlatosByRestaurante(1L)).thenReturn(List.of(plato1, plato2));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = pedidoService.crearPedido(request, 5L);

        assertEquals(5L, response.getIdCliente());
        assertEquals(PedidoEstado.PENDIENTE, response.getEstado());
        assertEquals(2, response.getListaIdsPlatos().size());
        verify(listOperations, times(1)).rightPush(anyString(), anyString());
    }

    @Test
    void shouldFailCreatePedidoWhenClienteTienePedidoActivo() {
        CreatePedidoRequest request = new CreatePedidoRequest();
        request.setRestauranteId(1L);
        request.setListaIdsPlatos(Set.of(10L));

        when(pedidoRepository.findByIdClienteAndEstadoIn(anyLong(), anyList())).thenReturn(List.of(new Pedido()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> pedidoService.crearPedido(request, 5L));

        assertTrue(exception.getMessage().contains("ya tiene un pedido activo"));
    }

    @Test
    void shouldCancelPendingPedido() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setIdCliente(5L);
        pedido.setIdRestaurante(1L);
        pedido.setEstado(PedidoEstado.PENDIENTE);
        pedido.setFechaCreacion(Instant.now());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pedidoService.cancelarPedido(pedidoId, 5L);

        assertEquals(PedidoEstado.CANCELADO, pedido.getEstado());
        verify(listOperations, times(1)).remove(anyString(), eq(0L), eq(pedidoId.toString()));
    }

    @Test
    void shouldGeneratePinAndSendNotificationWhenListo() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setIdCliente(5L);
        pedido.setIdRestaurante(1L);
        pedido.setEstado(PedidoEstado.EN_PREPARACION);
        pedido.setFechaCreacion(Instant.now());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = pedidoService.marcarListo(pedidoId);

        assertEquals(PedidoEstado.LISTO, response.getEstado());
        assertNotNull(response.getPinSeguridad());
        assertEquals(6, response.getPinSeguridad().length());
    }

    @Test
    void shouldDeliverPedidoWhenPinValid() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setIdCliente(5L);
        pedido.setIdRestaurante(1L);
        pedido.setEstado(PedidoEstado.LISTO);
        pedido.setPinSeguridad("123456");
        pedido.setFechaCreacion(Instant.now());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EntregarPedidoRequest request = new EntregarPedidoRequest();
        request.setPinSeguridad("123456");

        var response = pedidoService.entregarPedido(pedidoId, request);

        assertEquals(PedidoEstado.ENTREGADO, response.getEstado());
    }
}
