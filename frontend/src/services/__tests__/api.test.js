// src/services/__tests__/api.test.js
import api from '../api';

// Мокаем localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};

global.localStorage = localStorageMock;

describe('api', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should export axios instance with interceptors', () => {
    // Просто проверяем что api существует и имеет основные методы
    expect(api).toBeDefined();
    expect(typeof api.get).toBe('function');
    expect(typeof api.post).toBe('function');
    expect(typeof api.put).toBe('function');
    expect(typeof api.delete).toBe('function');
    expect(typeof api.interceptors.request.use).toBe('function');
  });

  test('should have baseURL configured', () => {
    // Проверяем что baseURL установлен
    expect(api.defaults.baseURL).toBe('http://localhost:8080');
  });

  test('should have Content-Type header configured', () => {
    // Проверяем заголовки
    expect(api.defaults.headers['Content-Type']).toBe('application/json');
  });

  test('should have request interceptor configured', () => {
    // Просто проверяем что интерцепторы существуют
    expect(api.interceptors.request).toBeDefined();
    expect(api.interceptors.response).toBeDefined();
  });
});