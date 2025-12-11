import { useState } from 'react';
import { Box, TextField, IconButton } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import api from '../services/api';

export default function MessageInput({ chatId }) {
  const [content, setContent] = useState('');

    if (typeof chatId !== 'number' && typeof chatId !== 'string') {
      console.error("❌ MessageInput: Invalid chatId type or value:", chatId);
      return <div>Error: Invalid chat</div>;
    }

    if (typeof chatId === 'object' && chatId !== null && chatId.$$typeof) {
      console.error("❌ MessageInput: chatId is a React object:", chatId);
      return <div>Error: Invalid chat</div>;
    }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;
    try {
      await api.post(`/chats/${chatId}/messages`, content, {
        headers: { 'Content-Type': 'text/plain' }
      });
      setContent('');
    } catch (err) {
      console.error('Failed to send message', err);
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