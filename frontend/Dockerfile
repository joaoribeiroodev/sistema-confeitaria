# Estágio 1: Build do Angular
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production

# Estágio 2: Servidor Web Nginx para os arquivos estáticos
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

RUN cat > /etc/nginx/conf.d/default.conf <<EOF
server {
  listen 80;
  client_max_body_size 10M;

  # Serve arquivos estáticos do Angular
  location / {
    root /usr/share/nginx/html;
    index index.html;
    try_files \$uri \$uri/ /index.html;
  }

  # Proxy de API para o backend
  location /api/ {
    proxy_pass http://backend:8080;
    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
    proxy_read_timeout 90s;
    proxy_connect_timeout 90s;
  }
}
EOF

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
