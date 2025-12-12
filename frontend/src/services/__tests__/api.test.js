// src/services/__tests__/api.test.js
import axios from 'axios';
import api from '../api';

// Мокаем axios.create
jest.mock('axios', () => ({
  create: jest.fn(() => ({
    interceptors: {
      request: {
        use: jest.fn(),
      },
    },
    defaults: {},
  })),
}));

describe('api', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should create axios instance with correct baseURL', () => {
    expect(axios.create).toHaveBeenCalledWith(
      expect.objectContaining({
        baseURL: 'http://localhost:8080',
      })
    );
  });

  test('should set Content-Type header', () => {
    expect(axios.create).toHaveBeenCalledWith(
      expect.objectContaining({
        headers: {
          'Content-Type': 'application/json',
        },
      })
    );
  });

  test('should add request interceptor to attach token', () => {
    // Проверим, что interceptors.request.use был вызван
    const mockInstance = {
      interceptors: {
        request: {
          use: jest.fn(),
        },
      },
    };
    require('axios').create.mockReturnValue(mockInstance);

    // Перезагружаем модуль, чтобы применить мок
    jest.resetModules();
    const freshApi = require('../api').default;

    expect(mockInstance.interceptors.request.use).toHaveBeenCalled();
  });
});