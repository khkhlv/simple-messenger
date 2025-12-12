// src/components/__tests__/MessageInput.test.jsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import MessageInput from '../MessageInput';
import api from '../../services/api';

jest.mock('../../services/api');

describe('MessageInput', () => {
  test('should submit message', async () => {
    api.post.mockResolvedValue();

    render(<MessageInput chatId={1} />);

    const input = screen.getByPlaceholderText(/Type a message.../i);
    fireEvent.change(input, { target: { value: 'Hello!' } });

    fireEvent.click(screen.getByRole('button'));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/chats/1/messages', 'Hello!', {
        headers: { 'Content-Type': 'text/plain' },
      });
    });
  });

  test('should not submit empty message', async () => {
    render(<MessageInput chatId={1} />);

    fireEvent.click(screen.getByRole('button'));

    expect(api.post).not.toHaveBeenCalled();
  });
});