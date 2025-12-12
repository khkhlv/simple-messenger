# Dockerfile.frontend.dev
FROM node:20-alpine

WORKDIR /app

# Устанавливаем дополнительные утилиты
RUN apk add --no-cache \
    git \
    curl \
    vim

# Копируем package.json
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем конфигурационные файлы
COPY vite.config.js .
COPY index.html .
COPY public ./public
COPY src ./src

# Создаем пользователя node для безопасности
RUN chown -R node:node /app
USER node

EXPOSE 5173

# Запускаем Vite dev server с поддержкой HMR
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]