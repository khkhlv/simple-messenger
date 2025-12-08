// src/context/AuthContext.jsx
import React, { createContext, useContext, useReducer, useEffect } from 'react';
import api from '../services/api';

// 1. Создаём контекст
const AuthContext = createContext();

// 2. Хук для использования контекста (ОБЯЗАТЕЛЬНО функция!)
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

// 3. Редьюсер
const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN':
      return { ...state, token: action.payload, isAuthenticated: true };
    case 'LOGOUT':
      return { ...state, token: null, isAuthenticated: false };
    default:
      return state;
  }
};

// 4. Провайдер
export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, {
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'),
  });

  // Синхронизация с localStorage
  useEffect(() => {
    if (state.token) {
      localStorage.setItem('token', state.token);
    } else {
      localStorage.removeItem('token');
    }
  }, [state.token]);

  // Методы
  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    dispatch({ type: 'LOGIN', payload: res.data.token });
  };

  const register = async (username, email, password) => {
    await api.post('/auth/register', { username, email, password });
  };

  const logout = () => {
    dispatch({ type: 'LOGOUT' });
  };

  return (
    <AuthContext.Provider value={{ ...state, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};