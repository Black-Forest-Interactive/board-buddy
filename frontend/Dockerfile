FROM node:22-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci --prefer-offline

COPY . .
RUN NX_DAEMON=false npx nx build portal-app --configuration=production

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/apps/portal-app/browser /usr/share/nginx/html
EXPOSE 80
