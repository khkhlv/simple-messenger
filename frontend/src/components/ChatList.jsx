import { useEffect, useState } from 'react';
import { List, ListItem, ListItemText, Divider } from '@mui/material';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function ChatList({ onSelectChat }) {
  const [chats, setChats] = useState([]);
  const { currentUser } = useAuth();

  useEffect(() => {
    const fetchChats = async () => {
      try {
        const res = await api.get('/chats');
        console.log("Fetched chats from /chats:", res.data);
        setChats(res.data);
      } catch (err) {
        console.error('Failed to load chats', err);
      }
    };
    fetchChats();
  }, []);

  return (
    <List>
      {chats.map((chat) => (
        <div key={chat.id} style={{ display: 'contents' }}> {/* ✅ Оберни в fragment, а не div */}
          <ListItem
            button={true}
            onClick={() => {
              console.log("Clicked chat item:", chat, typeof chat, chat.id);
              onSelectChat(chat);
            }}
          >
            <ListItemText
              primary={
                chat.participants
                  .filter(p => p !== currentUser?.username)
                  .join(', ') || 'Unknown'
              }
            />
          </ListItem>
          <Divider />
        </div>
      ))}
    </List>
  );
}