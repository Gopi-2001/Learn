# Step 1: Start with Node.js 20 on tiny Alpine Linux
FROM node:20-alpine

# Step 2: Add some info labels
LABEL maintainer="gk@example.com"
LABEL version="1.0.0"

# Step 3: Set working directory (all commands happen here)
WORKDIR /app

# Step 4: Copy package.json first (for smart caching)
COPY package.json .

# Step 5: Install dependencies
RUN npm install

# Step 6: Copy your app code
COPY server.js .

# Step 7: Set environment variables
ENV NODE_ENV=production
ENV PORT=3000

# Step 8: Document which port the app uses
EXPOSE 3000

# Step 9: Add a health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000', r => process.exit(r.statusCode === 200 ? 0 : 1))" || exit 1

# Step 10: Run as non-root user (security!)
RUN addgroup -S mygroup && adduser -S myuser -G mygroup
USER myuser

# Step 11: Start the app when container runs
CMD ["node", "server.js"]



