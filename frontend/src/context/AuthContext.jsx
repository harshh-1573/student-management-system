import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';
import toast from 'react-hot-toast';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('sms_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('sms_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('sms_token');
      if (storedToken) {
        try {
          const res = await authApi.getMe();
          if (res.success && res.data) {
            setUser(res.data);
            localStorage.setItem('sms_user', JSON.stringify(res.data));
          }
        } catch (err) {
          console.error("Auth initialization failed:", err);
          logout(false);
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (credentials) => {
    try {
      const res = await authApi.login(credentials);
      if (res.success && res.data) {
        const { token: jwtToken, ...userData } = res.data;
        setToken(jwtToken);
        setUser(userData);
        localStorage.setItem('sms_token', jwtToken);
        localStorage.setItem('sms_user', JSON.stringify(userData));
        toast.success(`Welcome back, ${userData.fullName}!`);
        return { success: true, user: userData };
      }
      return { success: false, message: res.message };
    } catch (error) {
      const msg = error.response?.data?.message || 'Login failed. Please check your credentials.';
      toast.error(msg);
      return { success: false, message: msg };
    }
  };

  const register = async (userData) => {
    try {
      const res = await authApi.register(userData);
      if (res.success && res.data) {
        const { token: jwtToken, ...userInfo } = res.data;
        setToken(jwtToken);
        setUser(userInfo);
        localStorage.setItem('sms_token', jwtToken);
        localStorage.setItem('sms_user', JSON.stringify(userInfo));
        toast.success('Registration successful! Welcome aboard.');
        return { success: true, user: userInfo };
      }
      return { success: false, message: res.message };
    } catch (error) {
      const msg = error.response?.data?.message || 'Registration failed.';
      toast.error(msg);
      return { success: false, message: msg };
    }
  };

  const logout = (notify = true) => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('sms_token');
    localStorage.removeItem('sms_user');
    if (notify) {
      toast.success('Logged out successfully');
    }
  };

  const hasRole = (roles) => {
    if (!user || !user.role) return false;
    if (Array.isArray(roles)) {
      return roles.includes(user.role);
    }
    return user.role === roles;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        isAuthenticated: !!token && !!user,
        isAdmin: user?.role === 'ROLE_ADMIN',
        isTeacher: user?.role === 'ROLE_TEACHER',
        isStudent: user?.role === 'ROLE_STUDENT',
        login,
        register,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
