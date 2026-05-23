import { useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';

export default function RegisterPage() {
  const [nombre, setNombre] = useState('');
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');

    try {
      // Los clientes siempre se registran con rol CLIENTE
      await register({ nombre, correo, password, role: 'CLIENTE' });
      navigate('/cliente');
    } catch (reason) {
      if (axios.isAxiosError(reason)) {
        const msg = reason.response?.data?.error ?? reason.response?.data?.message;
        setError(msg ?? 'No se pudo registrar. Verifica los datos e intenta de nuevo.');
      } else {
        setError('No se pudo registrar. Verifica los datos e intenta de nuevo.');
      }
    }
  };

  return (
    <div className="mx-auto mt-12 max-w-lg rounded-xl bg-white p-8 shadow-sm">
      <h1 className="text-2xl font-semibold">Crear cuenta</h1>
      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        <label className="block">
          <span className="text-sm font-medium text-slate-700">Nombre</span>
          <input
            value={nombre}
            onChange={(event) => setNombre(event.target.value)}
            required
            className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 focus:border-slate-900 focus:outline-none"
          />
        </label>
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
          Registrarme
        </button>
      </form>
      <p className="mt-6 text-sm text-slate-600">
        ¿Ya tienes cuenta?{' '}
        <Link to="/login" className="font-medium text-slate-900 hover:text-slate-700">
          Ingresa aquí
        </Link>
      </p>
    </div>
  );
}
