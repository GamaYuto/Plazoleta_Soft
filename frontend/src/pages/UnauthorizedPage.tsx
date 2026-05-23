import { Link } from 'react-router-dom';

export default function UnauthorizedPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="mx-auto max-w-md rounded-lg bg-white p-8 text-center shadow-lg">
        <h1 className="mb-4 text-4xl font-bold text-slate-900">403</h1>
        <p className="mb-6 text-lg text-slate-600">No tienes permiso para acceder a esta página.</p>
        <Link to="/" className="inline-block rounded-md bg-slate-900 px-6 py-2 text-white hover:bg-slate-700">
          Volver al inicio
        </Link>
      </div>
    </div>
  );
}
