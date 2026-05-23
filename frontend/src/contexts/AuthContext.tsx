import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { authApi } from '../api/axiosConfig';
import type { AuthUser } from '../utils/auth';
import { getUserFromToken, removeToken, setToken } from '../utils/auth';

interface LoginParams {
  correo: string;
  password: string;
}

interface RegisterParams {
  nombre: string;
  correo: string;
  password: string;
  role: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  initialized: boolean;
  login: (params: LoginParams) => Promise<void>;
  logout: () => void;
  register: (params: RegisterParams) => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    const parsed = getUserFromToken();
    setUser(parsed);
    setInitialized(true);
  }, []);

  const login = async ({ correo, password }: LoginParams) => {
    const response = await authApi.post('/auth/login', { correo, password });
    const token = response.data.token;
    setToken(token);
    setUser(getUserFromToken());
  };

  const logout = () => {
    removeToken();
    setUser(null);
  };

  const register = async ({ nombre, correo, password, role }: RegisterParams) => {
    const response = await authApi.post('/auth/register', { nombre, correo, password, role });
    // Si el backend devuelve un token, guardarlo y actualizar el usuario
    if (response.data.token) {
      setToken(response.data.token);
      setUser(getUserFromToken());
    }
  };

  const value = useMemo(
    () => ({ user, initialized, login, logout, register }),
    [user, initialized],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
