// src/pages/__tests__/Dashboard.test.jsx
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from '../Dashboard';
import { AuthProvider } from '../../context/AuthContext';

// Мок для компонентов которые могут отсутствовать
jest.mock('../../components/ChatList', () => () => <div data-testid="chat-list">ChatList</div>);
jest.mock('../../components/MessageList', () => () => <div data-testid="message-list">MessageList</div>);
jest.mock('../../components/MessageInput', () => () => <div data-testid="message-input">MessageInput</div>);
jest.mock('../../components/UserSearch', () => () => <div data-testid="user-search">UserSearch</div>);

describe('Dashboard', () => {
  // Отключить предупреждения React Router
  beforeAll(() => {
    jest.spyOn(console, 'warn').mockImplementation(() => {});
  });

  afterAll(() => {
    console.warn.mockRestore();
  });

  test('should render dashboard layout', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <Dashboard />
        </AuthProvider>
      </MemoryRouter>
    );

    // Проверяем наличие основных элементов
    expect(screen.getByTestId('chat-list')).toBeInTheDocument();
    expect(screen.getByTestId('user-search')).toBeInTheDocument();
  });
});