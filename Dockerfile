FROM node:current-alpine

# Use TCP port 8080, as default. Container reads ENV "PORT".
ENV PORT=8080
EXPOSE 8080

WORKDIR /usr/src/app

# Copy local code to the container image.
COPY package*.json ./

RUN npm install
# If you are building your code for production
# RUN npm ci --only=production

# Bundle app source
COPY . .

# Run the web server on container startup
CMD ["node", "main.js"]
