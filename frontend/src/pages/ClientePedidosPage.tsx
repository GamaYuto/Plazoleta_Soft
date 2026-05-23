import { useEffect, useState } from 'react';
import { pedidosApi } from '../api/axiosConfig';

interface Pedido {
  id: string;
  idRestaurante: number;
  estado: string;
  fechaCreacion: string;
  listaIdsPlatos: number[];
}

export default function ClientePedidosPage() {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    pedidosApi
      .get<Pedido[]>('/pedidos/mis-pedidos')
      .then((response) => setPedidos(response.data))
      .catch(() => setError('No se pudieron cargar tus pedidos.'));
  }, []);

  return (
    <div className="mx-auto mt-8 max-w-5xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Mis pedidos</h1>
        <p className="mt-2 text-slate-600">Revisa el estado de tus pedidos recientes.</p>
      </div>
      {error ? <div className="rounded-xl bg-red-50 p-4 text-red-700">{error}</div> : null}
      <div className="space-y-4">
        {pedidos.map((pedido) => (
          <article key={pedido.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm text-slate-500">Pedido ID: {pedido.id}</p>
            <p className="mt-2 text-lg font-semibold">Restaurante: {pedido.idRestaurante}</p>
            <p className="mt-1">Estado: {pedido.estado}</p>
            <p className="mt-1 text-sm text-slate-500">Creado: {new Date(pedido.fechaCreacion).toLocaleString()}</p>
            <p className="mt-2 text-slate-600">Platos: {pedido.listaIdsPlatos.join(', ') || 'Sin platos registrados'}</p>
          </article>
        ))}
      </div>
    </div>
  );
}
