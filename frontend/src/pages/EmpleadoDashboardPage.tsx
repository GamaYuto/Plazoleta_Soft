import { useEffect, useState } from 'react';
import { pedidosApi } from '../api/axiosConfig';
import { useAuth } from '../contexts/AuthContext';

interface PedidoResumen {
  id: string;
  idCliente: number;
  idRestaurante: number;
  estado: string;
  listaIdsPlatos: number[];
  pinSeguridad?: string;
}

export default function EmpleadoDashboardPage() {
  const [restaurantId, setRestaurantId] = useState('');
  const [pedidos, setPedidos] = useState<PedidoResumen[]>([]);
  const [pin, setPin] = useState('');
  const [message, setMessage] = useState('');
  const { user } = useAuth();

  const fetchPendientes = () => {
    if (!restaurantId) {
      setMessage('Ingresa el ID del restaurante para cargar pedidos.');
      return;
    }

    pedidosApi
      .get<PedidoResumen[]>(`/pedidos/pendientes?restauranteId=${restaurantId}`)
      .then((response) => {
        setPedidos(response.data);
        setMessage('');
      })
      .catch(() => setMessage('No se pudieron cargar los pedidos pendientes.'));
  };

  const handleAssign = async (pedidoId: string) => {
    try {
      await pedidosApi.patch(`/pedidos/${pedidoId}/asignar?empleadoId=${user?.id}`);
      fetchPendientes();
    } catch {
      setMessage('Error al asignar el pedido.');
    }
  };

  const handleMarkReady = async (pedidoId: string) => {
    try {
      await pedidosApi.patch(`/pedidos/${pedidoId}/listo`);
      fetchPendientes();
    } catch {
      setMessage('Error al marcar pedido como listo.');
    }
  };

  const handleDeliver = async (pedidoId: string) => {
    if (!pin) {
      setMessage('Debes ingresar el PIN de seguridad para entregar el pedido.');
      return;
    }

    try {
      await pedidosApi.patch(`/pedidos/${pedidoId}/entregar`, { pin });
      fetchPendientes();
      setPin('');
    } catch {
      setMessage('Error al entregar el pedido. Verifica el PIN.');
    }
  };

  useEffect(() => {
    if (restaurantId) fetchPendientes();
  }, [restaurantId]);

  return (
    <div className="mx-auto mt-8 max-w-6xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Dashboard de Empleado</h1>
        <p className="mt-2 text-slate-600">Cargar pedidos pendientes para el restaurante y gestionar su avance.</p>
      </div>
      <div className="grid gap-4 md:grid-cols-[1fr_2fr]">
        <div className="rounded-xl bg-white p-5 shadow-sm">
          <label className="block text-sm font-medium text-slate-700">ID del restaurante</label>
          <input
            value={restaurantId}
            onChange={(event) => setRestaurantId(event.target.value)}
            placeholder="Ej. 1"
            className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
          />
          <button onClick={fetchPendientes} className="mt-4 w-full rounded-md bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
            Cargar pedidos
          </button>
        </div>
        <div className="rounded-xl bg-white p-5 shadow-sm">
          <p className="text-sm text-slate-600">Empleado activo: {user?.correo || 'Sin sesión'}</p>
          <label className="mt-4 block text-sm font-medium text-slate-700">PIN de entrega</label>
          <input
            value={pin}
            onChange={(event) => setPin(event.target.value)}
            placeholder="1234"
            className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
          />
        </div>
      </div>
      {message ? <div className="rounded-xl bg-slate-50 p-4 text-slate-700">{message}</div> : null}
      <div className="space-y-4">
        {pedidos.map((pedido) => (
          <article key={pedido.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <div>
                <p className="text-sm text-slate-500">Pedido ID: {pedido.id}</p>
                <h2 className="text-xl font-semibold">Restaurante: {pedido.idRestaurante}</h2>
                <p className="mt-1 text-slate-600">Estado: {pedido.estado}</p>
                <p className="mt-1 text-slate-600">Cliente: {pedido.idCliente}</p>
              </div>
              <div className="flex flex-wrap gap-2">
                <button onClick={() => handleAssign(pedido.id)} className="rounded-md bg-blue-600 px-3 py-2 text-white hover:bg-blue-500">
                  Asignar
                </button>
                <button onClick={() => handleMarkReady(pedido.id)} className="rounded-md bg-amber-600 px-3 py-2 text-white hover:bg-amber-500">
                  Marcar listo
                </button>
                <button onClick={() => handleDeliver(pedido.id)} className="rounded-md bg-emerald-600 px-3 py-2 text-white hover:bg-emerald-500">
                  Entregar
                </button>
              </div>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
