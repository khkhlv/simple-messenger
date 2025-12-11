// src/context/AuthContext.jsx
import React, { createContext, useContext, useReducer, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN':
      return { ...state, token: action.payload, isAuthenticated: true };
    case 'LOGOUT':
      return { ...state, token: null, isAuthenticated: false, currentUser: null };
    case 'SET_CURRENT_USER':
      return { ...state, currentUser: action.payload };
    default:
      return state;
  }
};

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, {
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'),
    currentUser: null,
  });

  useEffect(() => {
    if (state.token && !state.currentUser) {
      const fetchProfile = async () => {
        try {
          const res = await api.get('/me');
          dispatch({ type: 'SET_CURRENT_USER', payload: res.data });
        } catch (err) {
          console.error("Failed to load profile on startup:", err);
        }
      };
      fetchProfile();
    }
  }, [state.token, state.currentUser]);

  useEffect(() => {
    if (state.token) {
      localStorage.setItem('token', state.token);
    } else {
      localStorage.removeItem('token');
    }
  }, [state.token]);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const token = res.data.token;
    dispatch({ type: 'LOGIN', payload: token });

    try {
      const profileRes = await api.get('/me');
      dispatch({ type: 'SET_CURRENT_USER', payload: profileRes.data });
    } catch (err) {
      console.error("Failed to load user profile:", err);
    }
  };

  const register = async (username, email, password) => {
    await api.post('/auth/register', { username, email, password });
    // После регистрации — автоматический логин
    await login(email, password);
  };

  const setCurrentUser = (user) => dispatch({ type: 'SET_CURRENT_USER', payload: user });

  const logout = () => {
    dispatch({ type: 'LOGOUT' });
  };

  return (
    <AuthContext.Provider value={{ ...state, login, register, logout, setCurrentUser }}>
      {children}
    </AuthContext.Provider>
  );
};