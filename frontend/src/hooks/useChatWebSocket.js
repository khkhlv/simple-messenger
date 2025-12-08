import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const useChatWebSocket = (chatId, onNewMessage) => {
  const stompClientRef = useRef(null);

  useEffect(() => {
    if (!chatId) return;

    // Закрываем предыдущее соединение
    if (stompClientRef.current) {
      stompClientRef.current.deactivate();
    }

    const socket = new SockJS('http://localhost:8080/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('token'),
      },
      onConnect: () => {
        console.log('Connected to WebSocket');
        client.subscribe(`/topic/chat/${chatId}`, (message) => {
          const msg = JSON.parse(message.body);
          onNewMessage(msg);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
      onWebSocketClose: () => {
        console.log('WebSocket connection closed');
      },
    });

    client.activate();
    stompClientRef.current = client;

    // Cleanup
    return () => {
      if (client.connected) {
        client.deactivate();
      }
    };
  }, [chatId, onNewMessage]);

  return stompClientRef;
};

export default useChatWebSocket;