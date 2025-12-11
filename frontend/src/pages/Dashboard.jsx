import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  Typography,
  Button,
  Divider,
  Menu,
  MenuItem,
} from '@mui/material';
import ChatList from '../components/ChatList';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';
import UserSearch from '../components/UserSearch';

const drawerWidth = 280;

export default function Dashboard() {
  const { logout, currentUser } = useAuth();
  const navigate = useNavigate();
  const [selectedChat, setSelectedChat] = useState(null);

  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    handleMenuClose();
  };

  const handleUserSelect = (chat) => {
    console.log("handleUserSelect received chat:", chat); // ✅ Добавь лог
    setSelectedChat(chat);
  };

  return (
    <Box sx={{ display: 'flex', height: '100vh' }}>
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

        <UserSearch onUserSelect={handleUserSelect} />

        <ChatList onSelectChat={setSelectedChat} />
      </Drawer>

      <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <AppBar position="static" color="default" elevation={1}>
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              {selectedChat
                ? selectedChat.participants.filter(p => p !== currentUser?.username).join(', ')
                : 'Select a chat or search user'}
            </Typography>

            {currentUser && (
              <>
                <Button color="inherit" onClick={handleMenuOpen}>
                  {currentUser.username}
                </Button>
                <Menu
                  anchorEl={anchorEl}
                  open={open}
                  onClose={handleMenuClose}
                >
                  <MenuItem onClick={handleLogout}>Logout</MenuItem>
                </Menu>
              </>
            )}
          </Toolbar>
        </AppBar>

        {selectedChat &&
          typeof selectedChat === 'object' &&
          selectedChat.id &&
          typeof selectedChat.id === 'number' &&
          !Array.isArray(selectedChat) ? (
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
            <Typography>Select a chat or search user to start messaging</Typography>
          </Box>
        )}
      </Box>
    </Box>
  );
}