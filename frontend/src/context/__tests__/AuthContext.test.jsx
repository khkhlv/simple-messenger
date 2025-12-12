// src/context/__tests__/AuthContext.test.jsx
import React from 'react';
import { render, act, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from '../AuthContext';
import api from '../../services/api';
import { MemoryRouter } from 'react-router-dom';

// Мокаем localStorage
const mockLocalStorage = (() => {
  let store = {};
  return {
    getItem: (key) => store[key] || null,
    setItem: (key, value) => {
      store[key] = value.toString();
    },
    removeItem: (key) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
});

// Мокаем api
jest.mock('../../services/api');

describe('AuthContext', () => {
  const TestComponent = () => {
    const context = useAuth();
    return <div>{context.isAuthenticated ? 'logged-in' : 'logged-out'}</div>;
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockLocalStorage.clear();
  });

  test('should initialize with no token', () => {
    const { getByText } = render(
      <MemoryRouter>
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      </MemoryRouter>
    );

    expect(getByText('logged-out')).toBeInTheDocument();
  });

  test('should set token after login', async () => {
    const mockToken = 'mock-jwt-token';
    const mockUser = { id: 1, username: 'testuser', email: 'test@example.com' };

    api.post.mockResolvedValueOnce({ data: { token: mockToken } });
    api.get.mockResolvedValueOnce({ data: mockUser });

    let authContext;
    const GetContext = () => {
      authContext = useAuth();
      return <div>test</div>;
    };

    render(
      <MemoryRouter>
        <AuthProvider>
          <GetContext />
        </AuthProvider>
      </MemoryRouter>
    );

    await act(async () => {
      await authContext.login('test@example.com', 'password');
    });

    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      password: 'password',
    });
    expect(localStorage.getItem('token')).toBe(mockToken);
    expect(authContext.isAuthenticated).toBe(true);
  });

  test('should logout and clear token', async () => {
    mockLocalStorage.setItem('token', 'mock-token');

    let authContext;
    const GetContext = () => {
      authContext = useAuth();
      return <div>test</div>;
    };

    render(
      <MemoryRouter>
        <AuthProvider>
          <GetContext />
        </AuthProvider>
      </MemoryRouter>
    );

    await act(async () => {
      authContext.logout();
    });

    expect(localStorage.getItem('token')).toBeNull();
    expect(authContext.isAuthenticated).toBe(false);
  });
});