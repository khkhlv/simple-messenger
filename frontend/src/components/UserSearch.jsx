import { useState } from 'react';
import { Box, TextField, Button, List, ListItem, ListItemText, ListItemSecondaryAction } from '@mui/material';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function UserSearch({ onUserSelect }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const { currentUser } = useAuth();

  const handleSearch = async () => {
    if (!query.trim()) {
      setResults([]);
      return;
    }
    try {
      const res = await api.get(`/users/search?query=${encodeURIComponent(query)}`);
      // ✅ Фильтруем текущего пользователя
      const filteredResults = res.data.filter(user => user.id !== currentUser?.id);
      setResults(filteredResults);
    } catch (err) {
      console.error('Search failed', err);
    }
  };

  const handleCreateChat = async (userId) => {
    try {
      const res = await api.post('/chats', [userId]);
      console.log("POST /chats returned:", res.data); // ✅ Добавь лог
      onUserSelect(res.data);
    } catch (err) {
      console.error('Failed to create chat', err);
    }
  };

  return (
    <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
      <Box sx={{ display: 'flex', gap: 1 }}>
        <TextField
          size="small"
          placeholder="Search user..."
          fullWidth
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button variant="outlined" onClick={handleSearch}>
          Find
        </Button>
      </Box>

      {results.length > 0 && (
        <List dense>
          {results.map((user) => (
            <ListItem key={user.id}>
              <ListItemText primary={user.username} secondary={user.email} />
              <ListItemSecondaryAction>
                <Button size="small" onClick={() => handleCreateChat(user.id)}>
                  Message
                </Button>
              </ListItemSecondaryAction>
            </ListItem>
          ))}
        </List>
      )}
    </Box>
  );
}