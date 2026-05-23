import { useEffect, useState } from 'react';
import { restauranteApi, trazabilidadApi } from '../api/axiosConfig';

interface Restaurante {
  id: number;
  nombre: string;
  direccion: string;
  idPropietario?: number;
}

interface Evento {
  id: string;
  pedidoId: string;
  estado: string;
  timestamp: string;
}

export default function PropietarioDashboardPage() {
  const [restaurantes, setRestaurantes] = useState<Restaurante[]>([]);
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    restauranteApi
      .get<Restaurante[]>('/restaurantes')
      .then((response) => setRestaurantes(response.data))
      .catch(() => setMessage('No se pudieron cargar los restaurantes.'));

    trazabilidadApi
      .get<Evento[]>('/api/trazabilidad/eventos')
      .then((response) => setEventos(response.data))
      .catch(() => setMessage('No se pudieron cargar los eventos de trazabilidad.'));
  }, []);

  return (
    <div className="mx-auto mt-8 max-w-6xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Dashboard de Propietario</h1>
        <p className="mt-2 text-slate-600">Administra restaurantes y visualiza métricas de trazabilidad.</p>
      </div>
      {message ? <div className="rounded-xl bg-slate-50 p-4 text-slate-700">{message}</div> : null}
      <section className="grid gap-4 md:grid-cols-2">
        <div className="rounded-xl bg-white p-5 shadow-sm">
          <h2 className="text-xl font-semibold">Restaurantes</h2>
          <ul className="mt-4 space-y-3">
            {restaurantes.map((restaurante) => (
              <li key={restaurante.id} className="rounded-xl border border-slate-200 p-4">
                <p className="font-semibold">{restaurante.nombre}</p>
                <p className="text-sm text-slate-600">{restaurante.direccion}</p>
              </li>
            ))}
          </ul>
        </div>
        <div className="rounded-xl bg-white p-5 shadow-sm">
          <h2 className="text-xl font-semibold">Eventos de Trazabilidad</h2>
          <div className="mt-4 space-y-3">
            {eventos.slice(0, 6).map((evento) => (
              <div key={evento.id} className="rounded-xl border border-slate-200 p-4">
                <p className="text-sm text-slate-500">Pedido: {evento.pedidoId}</p>
                <p className="mt-1 font-semibold">Estado: {evento.estado}</p>
                <p className="text-sm text-slate-500">{new Date(evento.timestamp).toLocaleString()}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
