FROM node:18-alpine AS builder
WORKDIR /app
ENV NODE_ENV=production

# install deps
COPY package.json ./
RUN npm install --no-audit --no-fund

# build app
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
