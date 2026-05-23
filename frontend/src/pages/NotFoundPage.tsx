import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="mx-auto max-w-3xl rounded-xl bg-white p-8 shadow-sm">
      <h1 className="text-3xl font-semibold">Página no encontrada</h1>
      <p className="mt-4 text-slate-600">La ruta que buscas no existe.</p>
      <Link to="/" className="mt-6 inline-block rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
        Volver al inicio
      </Link>
    </div>
  );
}
