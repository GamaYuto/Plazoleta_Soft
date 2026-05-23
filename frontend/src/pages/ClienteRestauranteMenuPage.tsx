import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { pedidosApi, restauranteApi } from '../api/axiosConfig';

interface Plato {
  id: number;
  nombre: string;
  precio?: number;
}

export default function ClienteRestauranteMenuPage() {
  const { id } = useParams<{ id: string }>();
  const restauranteId = Number(id);
  const [platos, setPlatos] = useState<Plato[]>([]);
  const [selectedPlatos, setSelectedPlatos] = useState<Set<number>>(new Set());
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!restauranteId) return;
    restauranteApi
      .get<Plato[]>(`/restaurantes/${restauranteId}/platos`)
      .then((response) => setPlatos(response.data))
      .catch(() => setMessage('No se pudo cargar el menú.'));
  }, [restauranteId]);

  const togglePlato = (platoId: number) => {
    setSelectedPlatos((current) => {
      const next = new Set(current);
      if (next.has(platoId)) {
        next.delete(platoId);
      } else {
        next.add(platoId);
      }
      return next;
    });
  };

  const handleOrder = async () => {
    if (!restauranteId || selectedPlatos.size === 0) {
      setMessage('Selecciona al menos un plato para ordenar.');
      return;
    }

    try {
      await pedidosApi.post('/pedidos', {
        restauranteId,
        listaIdsPlatos: Array.from(selectedPlatos),
      });
      setMessage('Pedido creado correctamente. Revisa tus pedidos.');
      setSelectedPlatos(new Set());
    } catch (error) {
      setMessage('Error al crear el pedido.');
    }
  };

  return (
    <div className="mx-auto mt-8 max-w-4xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Menú del restaurante</h1>
        <p className="mt-2 text-slate-600">Elige los platos que quieras ordenar.</p>
      </div>
      {message ? <div className="rounded-xl bg-slate-50 p-4 text-slate-700">{message}</div> : null}
      <div className="grid gap-4 md:grid-cols-2">
        {platos.map((plato) => (
          <button
            key={plato.id}
            type="button"
            onClick={() => togglePlato(plato.id)}
            className={`rounded-xl border px-4 py-4 text-left transition ${selectedPlatos.has(plato.id) ? 'border-slate-900 bg-slate-100' : 'border-slate-200 bg-white hover:border-slate-900'}`}
          >
            <h2 className="text-lg font-semibold">{plato.nombre}</h2>
            {plato.precio != null ? <p className="mt-2 text-slate-600">Precio: S/ {plato.precio}</p> : null}
          </button>
        ))}
      </div>
      <button onClick={handleOrder} className="rounded-md bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
        Crear pedido
      </button>
    </div>
  );
}
