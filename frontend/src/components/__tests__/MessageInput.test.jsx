// src/components/__tests__/MessageInput.test.jsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import MessageInput from '../MessageInput';
import api from '../../services/api';

// Мок для api
jest.mock('../../services/api', () => ({
  post: jest.fn(),
}));

// Простой мок для Material-UI
jest.mock('@mui/material', () => ({
  Box: ({ children, component, onSubmit, ...props }) => {
    if (component === 'form') {
      return (
        <form data-testid="message-form" onSubmit={onSubmit} {...props}>
          {children}
        </form>
      );
    }
    return <div {...props}>{children}</div>;
  },
  TextField: ({ value, onChange, placeholder, ...props }) => (
    <input
      data-testid="message-input"
      type="text"
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      {...props}
    />
  ),
  IconButton: ({ children, type, ...props }) => (
    <button data-testid="send-button" type={type} {...props}>
      {children}
    </button>
  ),
}));

// Мок для SendIcon
jest.mock('@mui/icons-material/Send', () => () => <span>SendIcon</span>);

// Мок для console
jest.spyOn(console, 'error').mockImplementation(() => {});

describe('MessageInput', () => {
  const mockChatId = 123;
  const mockContent = 'Hello, world!';

  beforeEach(() => {
    jest.clearAllMocks();
    api.post.mockResolvedValue({ data: {} });
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('should render message input form', () => {
    render(<MessageInput chatId={mockChatId} />);

    expect(screen.getByTestId('message-form')).toBeInTheDocument();
    expect(screen.getByTestId('message-input')).toBeInTheDocument();
    expect(screen.getByTestId('send-button')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Type a message...')).toBeInTheDocument();
    expect(screen.getByText('SendIcon')).toBeInTheDocument();
  });

  test('should update input value when typing', () => {
    render(<MessageInput chatId={mockChatId} />);

    const input = screen.getByTestId('message-input');
    fireEvent.change(input, { target: { value: mockContent } });

    expect(input).toHaveValue(mockContent);
  });

  test('should submit message when form is submitted', async () => {
    render(<MessageInput chatId={mockChatId} />);

    const input = screen.getByTestId('message-input');
    const form = screen.getByTestId('message-form');

    fireEvent.change(input, { target: { value: mockContent } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith(
        `/chats/${mockChatId}/messages`,
        mockContent,
        { headers: { 'Content-Type': 'text/plain' } }
      );
    });

    // Проверяем что поле очистилось после отправки
    expect(input).toHaveValue('');
  });

  test('should not submit empty message', async () => {
    render(<MessageInput chatId={mockChatId} />);

    const form = screen.getByTestId('message-form');
    fireEvent.submit(form);

    // API не должно вызываться для пустого сообщения
    expect(api.post).not.toHaveBeenCalled();
  });

  test('should not submit message with only whitespace', async () => {
    render(<MessageInput chatId={mockChatId} />);

    const input = screen.getByTestId('message-input');
    const form = screen.getByTestId('message-form');

    fireEvent.change(input, { target: { value: '   ' } });
    fireEvent.submit(form);

    // API не должно вызываться для сообщения из пробелов
    expect(api.post).not.toHaveBeenCalled();
    expect(input).toHaveValue('   ');
  });

  test('should handle string chatId', async () => {
    const stringChatId = '456';
    render(<MessageInput chatId={stringChatId} />);

    const input = screen.getByTestId('message-input');
    const form = screen.getByTestId('message-form');

    fireEvent.change(input, { target: { value: mockContent } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith(
        `/chats/${stringChatId}/messages`,
        mockContent,
        { headers: { 'Content-Type': 'text/plain' } }
      );
    });
  });

  test('should handle API error when sending message', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error');
    const error = new Error('Network error');
    api.post.mockRejectedValue(error);

    render(<MessageInput chatId={mockChatId} />);

    const input = screen.getByTestId('message-input');
    const form = screen.getByTestId('message-form');

    fireEvent.change(input, { target: { value: mockContent } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Failed to send message',
        error
      );
    });

    expect(input).toHaveValue(mockContent);
  });

  test('should show error for invalid chatId', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error');

    // Тестируем с невалидным chatId
    render(<MessageInput chatId={{ invalid: 'object' }} />);

    expect(screen.getByText('Error: Invalid chat')).toBeInTheDocument();
    expect(consoleErrorSpy).toHaveBeenCalledWith(
      expect.stringContaining('MessageInput:'),
      expect.anything()
    );
  });
});