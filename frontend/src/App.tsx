import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Container, TextField, Button, List, ListItem, ListItemText, Typography } from '@mui/material';

const App: React.FC = () => {
  const [messages, setMessages] = useState<{ id: number, content: string }[]>([]);
  const [newMessage, setNewMessage] = useState<string>('');

  // Загрузка сообщений при монтировании компонента
  useEffect(() => {
    fetchMessages();
  }, []);

  // Функция для загрузки сообщений
  const fetchMessages = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/messages');
      setMessages(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке сообщений:', error);
    }
  };

  // Функция для отправки нового сообщения
  const sendMessage = async () => {
    if (newMessage.trim() === '') return;

    try {
      await axios.post('http://localhost:8080/api/messages', { content: newMessage });
      setNewMessage('');
      fetchMessages(); // Обновляем список сообщений после отправки
    } catch (error) {
      console.error('Ошибка при отправке сообщения:', error);
    }
  };

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom>
        Мессенджер
      </Typography>
      <List>
        {messages.map((message) => (
          <ListItem key={message.id}>
            <ListItemText primary={message.content} />
          </ListItem>
        ))}
      </List>
      <TextField
        fullWidth
        variant="outlined"
        placeholder="Введите сообщение..."
        value={newMessage}
        onChange={(e) => setNewMessage(e.target.value)}
      />
      <Button
        variant="contained"
        color="primary"
        onClick={sendMessage}
        style={{ marginTop: '10px' }}
      >
        Отправить
      </Button>
    </Container>
  );
};

export default App;