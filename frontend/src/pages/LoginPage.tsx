import { useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function LoginPage() {
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');

    try {
      await login({ correo, password });
      navigate('/');
    } catch (reason) {
      setError('Credenciales inválidas. Intenta nuevamente.');
    }
  };

  return (
    <div className="mx-auto mt-12 max-w-lg rounded-xl bg-white p-8 shadow-sm">
      <h1 className="text-2xl font-semibold">Iniciar sesión</h1>
      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        <label className="block">
          <span className="text-sm font-medium text-slate-700">Correo</span>
          <input
            type="email"
            value={correo}
            onChange={(event) => setCorreo(event.target.value)}
            required
            className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
          />
        </label>
        <label className="block">
          <span className="text-sm font-medium text-slate-700">Contraseña</span>
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
          />
        </label>
        {error ? <p className="text-sm text-red-600">{error}</p> : null}
        <button type="submit" className="w-full rounded-md bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
          Entrar
        </button>
      </form>
      <p className="mt-6 text-sm text-slate-600">
        ¿No tienes cuenta?{' '}
        <Link to="/register" className="font-medium text-slate-900 hover:text-slate-700">
          Regístrate aquí
        </Link>
      </p>
    </div>
  );
}
