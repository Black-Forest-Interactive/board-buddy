FROM node:26.13.1-alpine AS builder
WORKDIR /app

COPY package*.json .npmrc ./
RUN npm ci --prefer-offline

COPY . .
RUN NX_DAEMON=false npx nx build portal-app --configuration=production

# runs as non-root user, therefore listens on 8080
FROM nginxinc/nginx-unprivileged:1.31.7-alpine
# base image is not rebuilt often enough, pull in Alpine security fixes
USER root
RUN apk upgrade --no-cache
USER 101
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/apps/portal-app/browser /usr/share/nginx/html
EXPOSE 8080
