import { useState } from 'react';
import React from 'react'
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  Divider,
  Paper,
  TextField,
  IconButton,
  InputAdornment,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import SendIcon from '@mui/icons-material/Send';
import ChatList from '../components/ChatList';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';

const drawerWidth = 280;

export default function Dashboard() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [selectedChat, setSelectedChat] = useState(null);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Box sx={{ display: 'flex', height: '100vh' }}>
      {/* Sidebar */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
        }}
      >
        <Toolbar>
          <Typography variant="h6" noWrap component="div">
            Chats
          </Typography>
        </Toolbar>
        <Divider />
        <ChatList onSelectChat={setSelectedChat} />
      </Drawer>

      {/* Main content */}
      <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <AppBar position="static" color="default" elevation={1}>
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              {selectedChat
                ? selectedChat.participants.join(', ')
                : 'Select a chat'}
            </Typography>
            <Button color="error" onClick={handleLogout}>
              Logout
            </Button>
          </Toolbar>
        </AppBar>

        {selectedChat ? (
          <>
            <MessageList chatId={selectedChat.id} />
            <MessageInput chatId={selectedChat.id} />
          </>
        ) : (
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              height: '100%',
              color: 'text.secondary',
            }}
          >
            <Typography>Select a chat to start messaging</Typography>
          </Box>
        )}
      </Box>
    </Box>
  );
}