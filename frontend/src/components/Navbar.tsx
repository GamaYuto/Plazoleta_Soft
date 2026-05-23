import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const navItems: Record<string, Array<{ label: string; to: string }>> = {
  CLIENTE: [
    { label: 'Restaurantes', to: '/cliente' },
    { label: 'Mis pedidos', to: '/cliente/pedidos' },
    { label: 'Trazabilidad', to: '/cliente/trazabilidad' },
  ],
  EMPLEADO: [
    { label: 'Pendientes', to: '/empleado' },
    { label: 'Asignados', to: '/empleado/asignados' },
  ],
  PROPIETARIO: [
    { label: 'Platos', to: '/propietario/platos' },
    { label: 'Empleados', to: '/propietario/empleados' },
    { label: 'Estadísticas', to: '/propietario/estadisticas' },
  ],
  ADMIN: [
    { label: 'Restaurantes', to: '/admin/restaurantes' },
    { label: 'Propietarios', to: '/admin/propietarios' },
  ],
};

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-slate-900 text-white shadow-sm">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6">
        <div>
          <Link to="/" className="text-xl font-semibold tracking-tight">
            Plazoleta</Link>
          <span className="ml-3 text-sm text-slate-300">Frontend</span>
        </div>

        <div className="flex items-center gap-4">
          {user ? (
            <>
              {navItems[user.role]?.map((item) => (
                <Link key={item.to} to={item.to} className="rounded-md px-3 py-2 text-sm hover:bg-slate-800">
                  {item.label}
                </Link>
              ))}
              <span className="rounded-full bg-slate-700 px-3 py-2 text-sm">{user.role}</span>
              <button onClick={handleLogout} className="rounded-md bg-red-600 px-3 py-2 text-sm hover:bg-red-500">
                Salir
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="rounded-md bg-slate-700 px-3 py-2 text-sm hover:bg-slate-600">
                Iniciar sesión
              </Link>
              <Link to="/register" className="rounded-md bg-slate-700 px-3 py-2 text-sm hover:bg-slate-600">
                Registrarse
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
