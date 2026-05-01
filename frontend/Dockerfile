# Use the lightweight Node.js 20 Alpine image
FROM node:20-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker cache
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Expose the port Next.js runs on
EXPOSE 3000

# Start the application in development mode with hot-reloading
CMD ["npm", "run", "dev"]
