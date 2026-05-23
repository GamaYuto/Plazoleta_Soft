import { useState } from 'react';
import type { FormEvent } from 'react';
import { authApi, restauranteApi } from '../api/axiosConfig';

interface RestauranteData {
  nombre: string;
  direccion: string;
  idPropietario: number | null;
}

export default function AdminDashboardPage() {
  const [ownerName, setOwnerName] = useState('');
  const [ownerCorreo, setOwnerCorreo] = useState('');
  const [ownerPassword, setOwnerPassword] = useState('');
  const [restauranteData, setRestauranteData] = useState<RestauranteData>({ nombre: '', direccion: '', idPropietario: null });
  const [message, setMessage] = useState('');

  const handleCreateOwner = async (event: FormEvent) => {
    event.preventDefault();
    try {
      const response = await authApi.post('/auth/register', {
        nombre: ownerName,
        correo: ownerCorreo,
        password: ownerPassword,
        role: 'PROPIETARIO',
      });
      setMessage(`Propietario creado con éxito: ${response.data.nombre || ownerName}`);
      setRestauranteData((current) => ({ ...current, idPropietario: response.data.id }));
    } catch {
      setMessage('No se pudo crear el propietario.');
    }
  };

  const handleCreateRestaurant = async (event: FormEvent) => {
    event.preventDefault();
    if (!restauranteData.idPropietario) {
      setMessage('Primero crea el propietario o ingresa su ID.');
      return;
    }

    try {
      await restauranteApi.post('/restaurantes', restauranteData);
      setMessage('Restaurante creado correctamente.');
      setRestauranteData({ nombre: '', direccion: '', idPropietario: null });
    } catch {
      setMessage('No se pudo crear el restaurante.');
    }
  };

  return (
    <div className="mx-auto mt-8 max-w-5xl space-y-6">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold">Dashboard de Administrador</h1>
        <p className="mt-2 text-slate-600">Crea propietarios y restaurantes desde el panel de administración.</p>
      </div>
      {message ? <div className="rounded-xl bg-slate-50 p-4 text-slate-700">{message}</div> : null}
      <div className="grid gap-6 lg:grid-cols-2">
        <section className="rounded-xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold">Crear propietario</h2>
          <form onSubmit={handleCreateOwner} className="mt-5 space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700">Nombre</label>
              <input
                value={ownerName}
                onChange={(event) => setOwnerName(event.target.value)}
                required
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700">Correo</label>
              <input
                type="email"
                value={ownerCorreo}
                onChange={(event) => setOwnerCorreo(event.target.value)}
                required
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700">Contraseña</label>
              <input
                type="password"
                value={ownerPassword}
                onChange={(event) => setOwnerPassword(event.target.value)}
                required
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
            </div>
            <button type="submit" className="rounded-md bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
              Crear propietario
            </button>
          </form>
        </section>

        <section className="rounded-xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold">Crear restaurante</h2>
          <form onSubmit={handleCreateRestaurant} className="mt-5 space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700">Nombre</label>
              <input
                value={restauranteData.nombre}
                onChange={(event) => setRestauranteData({ ...restauranteData, nombre: event.target.value })}
                required
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700">Dirección</label>
              <input
                value={restauranteData.direccion}
                onChange={(event) => setRestauranteData({ ...restauranteData, direccion: event.target.value })}
                required
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700">ID del propietario</label>
              <input
                type="number"
                value={restauranteData.idPropietario ?? ''}
                onChange={(event) => setRestauranteData({ ...restauranteData, idPropietario: Number(event.target.value) || null })}
                className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
              />
              <p className="mt-2 text-sm text-slate-500">Puedes usar el ID del propietario generado al crear una cuenta.</p>
            </div>
            <button type="submit" className="rounded-md bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
              Crear restaurante
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}
