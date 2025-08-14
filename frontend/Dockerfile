# Stage 1: Build React app
FROM node:20 AS build
WORKDIR /app

ARG VITE_APP_API
ENV VITE_APP_API=$VITE_APP_API

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve static files using Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
