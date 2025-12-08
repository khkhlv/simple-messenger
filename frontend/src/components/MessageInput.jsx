import { useState } from 'react';
import { Box, TextField, IconButton } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import api from '../services/api';
import React from 'react'

export default function MessageInput({ chatId }) {
  const [content, setContent] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;
    try {
      await api.post(`/chats/${chatId}/messages`, content, {
        headers: { 'Content-Type': 'text/plain' }
      });
      setContent('');
    } catch (err) {
      console.error('Failed to send message');
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ p: 1, backgroundColor: '#fff' }}>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <TextField
          fullWidth
          size="small"
          placeholder="Type a message..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          sx={{ mr: 1 }}
        />
        <IconButton type="submit" color="primary">
          <SendIcon />
        </IconButton>
      </Box>
    </Box>
  );
}