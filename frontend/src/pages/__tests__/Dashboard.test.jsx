// src/pages/__tests__/Dashboard.test.jsx
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import Dashboard from '../Dashboard';
import { AuthProvider } from '../../context/AuthContext';
import { MemoryRouter } from 'react-router-dom';

jest.mock('../../context/AuthContext', () => ({
  useAuth: () => ({
    logout: jest.fn(),
    currentUser: { username: 'testuser' },
  }),
}));

jest.mock('../../components/ChatList', () => () => <div>ChatList</div>);
jest.mock('../../components/MessageList', () => ({ chatId }) => <div>MessageList {chatId}</div>);
jest.mock('../../components/MessageInput', () => ({ chatId }) => <div>MessageInput {chatId}</div>);
jest.mock('../../components/UserSearch', () => () => <div>UserSearch</div>);

describe('Dashboard', () => {
  test('should render dashboard layout', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <Dashboard />
        </AuthProvider>
      </MemoryRouter>
    );

    expect(screen.getByText('Chats')).toBeInTheDocument();
    expect(screen.getByText('ChatList')).toBeInTheDocument();
    expect(screen.getByText('UserSearch')).toBeInTheDocument();
  });
});