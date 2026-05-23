import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { restauranteApi } from '../api/axiosConfig';

interface Restaurante {
  id: number;
  nombre: string;
  direccion: string;
  idPropietario?: number;
}

export default function ClienteHomePage() {
  const [restaurantes, setRestaurantes] = useState<Restaurante[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    restauranteApi
      .get<Restaurante[]>('/restaurantes')
      .then((response) => setRestaurantes(response.data))
      .catch(() => setError('No se pudieron cargar los restaurantes.'));
  }, []);

  return (
    <div className="mx-auto mt-8 max-w-5xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Restaurantes disponibles</h1>
        <p className="mt-2 text-slate-600">Selecciona un restaurante para ver su menú y crear un pedido.</p>
      </div>
      {error ? <div className="rounded-xl bg-red-50 p-4 text-red-700">{error}</div> : null}
      <div className="grid gap-4 md:grid-cols-2">
        {restaurantes.map((restaurante) => (
          <article key={restaurante.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between gap-4">
              <div>
                <h2 className="text-xl font-semibold">{restaurante.nombre}</h2>
                <p className="mt-1 text-slate-600">{restaurante.direccion}</p>
              </div>
              <Link
                to={`/cliente/restaurantes/${restaurante.id}`}
                className="rounded-md bg-slate-900 px-4 py-2 text-sm text-white hover:bg-slate-700"
              >
                Ver menú
              </Link>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
