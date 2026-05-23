import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ProtectedRoute } from '../components/ProtectedRoute';
import AdminDashboardPage from '../pages/AdminDashboardPage';
import ClienteHomePage from '../pages/ClienteHomePage';
import ClientePedidosPage from '../pages/ClientePedidosPage';
import ClienteRestauranteMenuPage from '../pages/ClienteRestauranteMenuPage';
import EmpleadoDashboardPage from '../pages/EmpleadoDashboardPage';
import LoginPage from '../pages/LoginPage';
import NotFoundPage from '../pages/NotFoundPage';
import PropietarioDashboardPage from '../pages/PropietarioDashboardPage';
import RegisterPage from '../pages/RegisterPage';
import UnauthorizedPage from '../pages/UnauthorizedPage';

function HomeRedirect() {
  const { user, initialized } = useAuth();

  if (!initialized) {
    return <div className="p-4">Cargando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role === 'CLIENTE') return <Navigate to="/cliente" replace />;
  if (user.role === 'EMPLEADO') return <Navigate to="/empleado" replace />;
  if (user.role === 'PROPIETARIO') return <Navigate to="/propietario" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
  return <Navigate to="/login" replace />;
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomeRedirect />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />
      <Route path="/cliente" element={<ProtectedRoute allowedRoles={["CLIENTE"]}><ClienteHomePage /></ProtectedRoute>} />
      <Route path="/cliente/pedidos" element={<ProtectedRoute allowedRoles={["CLIENTE"]}><ClientePedidosPage /></ProtectedRoute>} />
      <Route path="/cliente/restaurantes/:id" element={<ProtectedRoute allowedRoles={["CLIENTE"]}><ClienteRestauranteMenuPage /></ProtectedRoute>} />
      <Route path="/empleado" element={<ProtectedRoute allowedRoles={["EMPLEADO"]}><EmpleadoDashboardPage /></ProtectedRoute>} />
      <Route path="/propietario" element={<ProtectedRoute allowedRoles={["PROPIETARIO"]}><PropietarioDashboardPage /></ProtectedRoute>} />
      <Route path="/admin" element={<ProtectedRoute allowedRoles={["ADMIN"]}><AdminDashboardPage /></ProtectedRoute>} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
