import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';

const container = document.getElementById('root');
const root = createRoot(container!); // Используем createRoot вместо render

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);