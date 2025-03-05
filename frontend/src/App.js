import React, { useState, useEffect } from 'react';
import axios from 'axios';
import MessageForm from './components/MessageForm';
import MessageList from './components/MessageList';
import './App.css';

const App = () => {
    const [messages, setMessages] = useState([]);

    // Загрузка сообщений при монтировании компонента
    useEffect(() => {
        fetchMessages();
    }, []);

    // Получение сообщений
    const fetchMessages = async () => {
        const response = await axios.get('/messages/all');
        setMessages(response.data);
    };

    // Отправка сообщения
    const handleSend = async (message) => {
        await axios.post('/messages/send', null, {
            params: {
                sender: message.sender,
                recipient: message.recipient,
                content: message.content,
            },
        });
        fetchMessages(); // Обновляем список сообщений
    };

    // Удаление сообщения
    const handleDelete = async (id) => {
        await axios.delete(`/messages/${id}`);
        fetchMessages(); // Обновляем список сообщений
    };

    return (
        <div className="App">
            <h1>Messenger</h1>
            <MessageForm onSend={handleSend} />
            <MessageList messages={messages} onDelete={handleDelete} />
        </div>
    );
};

export default App;