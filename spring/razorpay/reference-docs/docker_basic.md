---

# 🚀 Docker Notes for Beginners

## 1. What is Docker?
- **Docker** is a platform to build, ship, and run applications inside **containers**.
- A **container** is a lightweight, isolated environment that packages your app + dependencies.
- Think of it as:
    - **VMs** → heavy, full OS
    - **Containers** → lightweight, share OS kernel, faster

---

## 2. Installing Docker
### On Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker
docker --version
```

### On Windows/Mac
- Install **Docker Desktop** from docker.com [(docker.com)]("https://docs.docker.com/desktop/setup/install/windows-install/").
- Verify installation:
```bash
docker --version
```

---

## 3. Day-to-Day Docker Commands
| Command | Purpose |
|---------|---------|
| `docker --version` | Check Docker version |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers (including stopped) |
| `docker images` | List available images |
| `docker pull <image>` | Download image from Docker Hub |
| `docker run -d -p 8080:8080 <image>` | Run container in background, map port |
| `docker stop <container_id>` | Stop container |
| `docker rm <container_id>` | Remove container |
| `docker rmi <image_id>` | Remove image |
| `docker exec -it <container_id> bash` | Enter container shell |

---

## 4. Dockerfile 📝
A **Dockerfile** is a script with instructions to build a Docker image.

### Example: Spring Boot App
**Project structure:**
```
spring-docker-demo/
 ├── src/
 ├── target/
 ├── Dockerfile
 └── pom.xml
```

**Dockerfile:**
```dockerfile
# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/spring-docker-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build & Run
```bash
# Build image
docker build -t spring-docker-demo .

# Run container
docker run -d -p 8080:8080 spring-docker-demo
```

---

## 5. Docker Compose ⚙️
- **Docker Compose** helps run **multi-container applications** using a single YAML file.
- Example: Spring Boot + MySQL.

**docker-compose.yml**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/demo
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root

  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: demo
    ports:
      - "3306:3306"
```

### Run with Compose
```bash
docker-compose up -d
docker-compose down
```

---

## 6. Hands-On Workflow (Java + Spring Boot)
1. **Create Spring Boot app** (REST API).
2. **Build JAR**:
   ```bash
   mvn clean package
   ```
3. **Write Dockerfile** (as shown above).
4. **Build image**:
   ```bash
   docker build -t spring-docker-demo .
   ```
5. **Run with Docker**:
   ```bash
   docker run -d -p 8080:8080 spring-docker-demo
   ```
6. **Extend with Docker Compose** (add MySQL service).
7. **Run with Compose**:
   ```bash
   docker-compose up -d
   ```

---

## 7. Common Debugging Commands
```bash
docker logs <container_id>       # View logs
docker inspect <container_id>    # Inspect details
docker network ls                # List networks
docker volume ls                 # List volumes
```

---

## 8. Quick Mental Model
- **Image** → Recipe (immutable snapshot)
- **Container** → Running instance of image
- **Dockerfile** → Instructions to build image
- **Docker Compose** → Orchestrator for multiple containers

---