import { useAuthStore } from '../store/authStore';

export function useAuth() {
  const { token, user, setToken, setUser, logout } = useAuthStore();
  return { token, user, setToken, setUser, logout, isAuthenticated: !!token };
}
