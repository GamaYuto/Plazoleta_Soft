export const TOKEN_KEY = 'plazoleta_token';

export interface AuthUser {
  id: number;
  role: string;
  correo?: string;
  nombre?: string;
}

const decodeBase64 = (value: string) => {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4);
  return atob(padded);
};

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (token: string) => localStorage.setItem(TOKEN_KEY, token);
export const removeToken = () => localStorage.removeItem(TOKEN_KEY);
export const isAuthenticated = () => !!getToken();

export const getUserFromToken = (): AuthUser | null => {
  const token = getToken();
  if (!token) return null;

  try {
    const [, payload] = token.split('.');
    if (!payload) return null;
    const json = decodeBase64(payload);
    const parsed = JSON.parse(json);
    return {
      id: Number(parsed.id),
      role: String(parsed.role),
      correo: parsed.sub || parsed.correo || undefined,
      nombre: parsed.nombre || undefined,
    };
  } catch {
    return null;
  }
};
