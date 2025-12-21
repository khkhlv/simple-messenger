// src/components/__tests__/ChatList.test.jsx
import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import ChatList from '../ChatList';
import api from '../../services/api';

// Мок для api
jest.mock('../../services/api', () => ({
  get: jest.fn(),
}));

// Мок для Material-UI
jest.mock('@mui/material', () => ({
  List: ({ children, ...props }) => <div {...props}>{children}</div>,
  ListItem: ({ children, button, onClick, ...props }) => (
    <div onClick={onClick} {...props}>
      {children}
    </div>
  ),
  ListItemText: ({ primary, ...props }) => (
    <div {...props} data-testid="list-item-text">
      {primary}
    </div>
  ),
  Divider: () => <hr />,
}));

// Мок для AuthContext
jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

const mockUseAuth = require('../../context/AuthContext').useAuth;

describe('ChatList', () => {
  const mockChats = [
    { id: 1, participants: ['user1', 'user2'] },
    { id: 2, participants: ['user1', 'user3'] },
    { id: 3, participants: ['user2'] }, // Этот чат содержит только user2
  ];

  const mockCurrentUser = { username: 'user1' };
  const mockOnSelectChat = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockUseAuth.mockReturnValue({ currentUser: mockCurrentUser });
    api.get.mockResolvedValue({ data: mockChats });
  });

  test('should fetch and display chats on mount', async () => {
    render(<ChatList onSelectChat={mockOnSelectChat} />);

    expect(api.get).toHaveBeenCalledWith('/chats');

    // Используем getAllByText для поиска нескольких элементов
    await waitFor(() => {
      const user2Elements = screen.getAllByText('user2');
      expect(user2Elements.length).toBe(2); // user2 появляется в двух чатах
      expect(screen.getByText('user3')).toBeInTheDocument();
    });

    // user1 не должен отображаться
    expect(screen.queryByText('user1')).not.toBeInTheDocument();
  });

  test('should filter out current user from participant list', async () => {
    render(<ChatList onSelectChat={mockOnSelectChat} />);

    await waitFor(() => {
      const user2Elements = screen.getAllByText('user2');
      expect(user2Elements.length).toBe(2);
      expect(screen.getByText('user3')).toBeInTheDocument();
    });

    expect(screen.queryByText('user1')).not.toBeInTheDocument();
  });

  test('should call onSelectChat when chat item is clicked', async () => {
    render(<ChatList onSelectChat={mockOnSelectChat} />);

    await waitFor(() => {
      expect(screen.getAllByText('user2').length).toBe(2);
    });

    // Берем первый элемент user2
    const firstUser2Element = screen.getAllByText('user2')[0];
    fireEvent.click(firstUser2Element);

    expect(mockOnSelectChat).toHaveBeenCalledWith(mockChats[0]);
  });

  test('should display "Unknown" if no other participants', async () => {
    mockUseAuth.mockReturnValue({ currentUser: { username: 'user2' } });
    api.get.mockResolvedValue({
      data: [{ id: 4, participants: ['user2'] }]
    });

    render(<ChatList onSelectChat={mockOnSelectChat} />);

    await waitFor(() => {
      expect(screen.getByText('Unknown')).toBeInTheDocument();
    });
  });

  test('should handle empty chats array', async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<ChatList onSelectChat={mockOnSelectChat} />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalled();
    });

    // Не должно быть текста участников
    expect(screen.queryByText('user2')).not.toBeInTheDocument();
    expect(screen.queryByText('user3')).not.toBeInTheDocument();
  });

  test('should handle API error gracefully', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error');
    api.get.mockRejectedValue(new Error('Network error'));

    render(<ChatList onSelectChat={mockOnSelectChat} />);

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Failed to load chats',
        expect.any(Error)
      );
    });

    expect(screen.queryByText('user2')).not.toBeInTheDocument();
  });
});