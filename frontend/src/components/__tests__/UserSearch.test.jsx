// src/components/__tests__/UserSearch.test.jsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import UserSearch from '../UserSearch';
import api from '../../services/api';
import { AuthProvider } from '../../context/AuthContext';

jest.mock('../../services/api');

describe('UserSearch', () => {
  const mockOnUserSelect = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should search users', async () => {
    const mockUsers = [
      { id: 1, username: 'testuser', email: 'test@example.com' },
    ];
    api.get.mockResolvedValue({ data: mockUsers });

    render(
      <AuthProvider>
        <UserSearch onUserSelect={mockOnUserSelect} />
      </AuthProvider>
    );

    const input = screen.getByPlaceholderText(/Search user.../i);
    fireEvent.change(input, { target: { value: 'test' } });

    fireEvent.click(screen.getByText(/Find/i));

    await waitFor(() => {
      expect(screen.getByText('testuser')).toBeInTheDocument();
    });
  });
});