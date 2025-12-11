import { useEffect, useState } from 'react';
import { Box, TextField, InputAdornment, IconButton, Paper, Typography } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import api from '../services/api';

export default function MessageList({ chatId }) {
  const [messages, setMessages] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  console.log("SearchIcon type:", typeof SearchIcon, SearchIcon);
    console.log("MessageList received chatId:", chatId, typeof chatId, chatId);

  if (typeof chatId !== 'number' && typeof chatId !== 'string') {
    console.error("❌ MessageList: Invalid chatId type or value:", chatId);
    return <div>Error: Invalid chat</div>;
  }

  // Также проверь, что chatId — не React-объект
  if (typeof chatId === 'object' && chatId !== null && chatId.$$typeof) {
    console.error("❌ MessageList: chatId is a React object:", chatId);
    return <div>Error: Invalid chat</div>;
  }

  useEffect(() => {
    if (!chatId) return;
    const loadMessages = async () => {
      try {
        const res = await api.get(`/chats/${chatId}/messages`);
        console.log(`Messages for chat ${chatId}:`, res.data);
        setMessages(res.data);
      } catch (err) {
        console.error('Failed to load messages', err);
      }
    };
    loadMessages();
  }, [chatId]);

  const handleNewMessage = (newMsg) => {
    setMessages(prev => [...prev, newMsg]);
    if (searchQuery) {
      if (newMsg.content.toLowerCase().includes(searchQuery.toLowerCase())) {
        setSearchResults(prev => [...prev, newMsg]);
      }
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    try {
      const res = await api.get(`/chats/${chatId}/messages/search?query=${searchQuery}`);
      setSearchResults(res.data);
    } catch (err) {
      console.error('Search failed', err);
    }
  };

  const displayed = searchQuery ? searchResults : messages;

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Box sx={{ p: 1, backgroundColor: '#fff' }}>
        <TextField
          fullWidth
          size="small"
          placeholder="Search in chat..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton onClick={handleSearch} size="small">
                  <SearchIcon />
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </Box>

      <Box
        sx={{
          flexGrow: 1,
          overflowY: 'auto',
          p: 2,
          backgroundColor: '#f5f5f5',
        }}
      >
        {displayed.length === 0 ? (
          <Typography color="text.secondary">No messages</Typography>
        ) : (
          displayed.map((msg) => (
            <Paper
              key={msg.id}
              sx={{
                mb: 1.5,
                p: 1.5,
                maxWidth: '70%',
                ml: msg.sender === 'me' ? 'auto' : 0,
                backgroundColor: msg.sender === 'me' ? '#e3f2fd' : '#fff',
              }}
            >
              <Typography variant="caption" color="text.secondary" display="block">
                {msg.sender} • {new Date(msg.timestamp).toLocaleString()}
              </Typography>
              <Typography>{msg.content}</Typography>
            </Paper>
          ))
        )}
      </Box>
    </Box>
  );
}