// src/components/__tests__/ChatList.test.jsx
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import ChatList from '../ChatList';
import api from '../../services/api';
import { AuthProvider } from '../../context/AuthContext';

jest.mock('../../services/api');

describe('ChatList', () => {
  const mockOnSelectChat = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should fetch and display chats', async () => {
    const mockChats = [
      { id: 1, participants: ['user1', 'user2'] },
      { id: 2, participants: ['user1'] },
    ];
    api.get.mockResolvedValue({ data: mockChats });

    render(
      <AuthProvider>
        <ChatList onSelectChat={mockOnSelectChat} />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('user2')).toBeInTheDocument();
    });
  });
});