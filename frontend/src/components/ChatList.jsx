// src/components/ChatList.jsx
import { useEffect, useState } from 'react';
import { List, ListItem, ListItemText, Divider } from '@mui/material';
import api from '../services/api';
import React from 'react'

export default function ChatList({ onSelectChat }) {
  const [chats, setChats] = useState([]);

  useEffect(() => {
    const fetchChats = async () => {
      try {
        const res = await api.get('/chats');
        setChats(res.data);
      } catch (err) {
        console.error('Failed to load chats');
      }
    };
    fetchChats();
  }, []);

  return (
    <List>
      {chats.map((chat) => (
        <div key={chat.id}>
          <ListItem button onClick={() => onSelectChat(chat)}>
            <ListItemText
              primary={chat.participants.filter(p => p !== 'me').join(', ') || 'Unknown'}
            />
          </ListItem>
          <Divider />
        </div>
      ))}
    </List>
  );
}