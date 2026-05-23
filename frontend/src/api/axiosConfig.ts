import axios from 'axios';
import { getToken } from '../utils/auth';

const authApi = axios.create({ baseURL: import.meta.env.VITE_API_AUTH_URL });
const restauranteApi = axios.create({ baseURL: import.meta.env.VITE_API_RESTAURANTE_URL });
const pedidosApi = axios.create({ baseURL: import.meta.env.VITE_API_PEDIDOS_URL });
const trazabilidadApi = axios.create({ baseURL: import.meta.env.VITE_API_TRAZABILIDAD_URL });
const notificacionesApi = axios.create({ baseURL: import.meta.env.VITE_API_NOTIFICACIONES_URL });

const attachToken = (config: any) => {
  const token = getToken();
  if (token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${token}`,
    };
  }
  return config;
};

[authApi, restauranteApi, pedidosApi, trazabilidadApi, notificacionesApi].forEach((instance) => {
  instance.interceptors.request.use(attachToken, (error) => Promise.reject(error));
});

export { authApi, restauranteApi, pedidosApi, trazabilidadApi, notificacionesApi };
