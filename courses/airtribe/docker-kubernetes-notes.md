# Docker & Kubernetes — Complete Interview Notes

> **Backend Developer Interview Preparation & Quick Revision Guide**
> Covers: Architecture · Commands · YAML · Networking · Storage · CI/CD · Best Practices · 50+ Interview Q&A

---

## Table of Contents

### Part 1: Docker

1. Introduction
2. Docker Architecture
3. Images & Containers
4. Dockerfile
5. Essential Docker Commands
6. Volumes
7. Networking
8. Docker Compose
9. Multi-stage Builds
10. Docker Best Practices
11. Troubleshooting Docker

### Part 2: Kubernetes

12. Introduction to Kubernetes
13. Kubernetes Architecture
14. Core Kubernetes Objects
15. Kubernetes Networking
16. Storage
17. Scaling & Updates
18. Kubernetes YAML
19. Essential kubectl Commands
20. Docker + Kubernetes Workflow
21. CI/CD Integration
22. Best Practices
23. Common Interview Questions
24. Quick Revision Cheat Sheet

---

# Part 1: Docker

---

## 1. Introduction

### What is Docker?

- An **open-source platform** for building, shipping, and running applications in **containers**.
- A container packages your app + all its dependencies (libraries, runtime, config) into a single, portable unit.
- Write once, run anywhere — on any machine that has Docker installed.

### Why Docker Was Created

- Pre-Docker: "It works on my machine" was a constant problem.
- Deployments broke because dev, staging, and prod environments differed (OS versions, library versions, paths).
- Docker was created to **eliminate environment inconsistency** and make deployments reproducible.

### Problems Docker Solves

| Problem | Docker Solution |
| --- | --- |
| Environment inconsistency | Same container image across all environments |
| Dependency conflicts | Isolated containers; each has its own deps |
| Slow VM provisioning | Containers start in milliseconds |
| Onboarding friction | `docker compose up` replaces 10-page setup guides |
| Resource wastage | Containers share host OS kernel; lightweight |
| Scaling difficulty | Spin up multiple container instances instantly |

### Containers vs Virtual Machines

```
Virtual Machine                    Container
─────────────────────              ─────────────────────
  App A   │  App B                   App A   │  App B
─────────────────────              ─────────────────────
 Libs     │  Libs                   Libs     │  Libs
─────────────────────              ─────────────────────
Guest OS  │ Guest OS                ┌───────────────────┐
─────────────────────              │   Docker Engine    │
     Hypervisor                    └───────────────────┘
─────────────────────              ─────────────────────
       Host OS                            Host OS
─────────────────────              ─────────────────────
      Hardware                           Hardware
```

| Feature | Container | Virtual Machine |
| --- | --- | --- |
| Startup time | Milliseconds | Minutes |
| Size | MBs | GBs |
| OS | Shares host kernel | Full guest OS |
| Isolation | Process-level | Hardware-level |
| Performance | Near-native | Overhead from hypervisor |
| Portability | Very high | Lower (OS-specific images) |
| Security | Slightly lower isolation | Stronger isolation |
| Use case | Microservices, CI/CD | Legacy apps, full OS isolation |

### Benefits and Limitations

**Benefits:**
- Consistent, reproducible environments
- Fast startup and lightweight resource usage
- Easy scaling and horizontal replication
- Strong ecosystem (Docker Hub, Compose, Swarm, K8s)
- Simplifies CI/CD pipelines

**Limitations:**
- Containers share the host kernel — less isolated than VMs
- GUI applications are complex to containerize
- Persistent storage requires extra setup
- Not ideal for kernel-level or hardware-specific workloads
- Windows containers behave differently from Linux containers

---

## 2. Docker Architecture

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Docker Client                            │
│              (CLI: docker build / run / push ...)               │
└──────────────────────────┬──────────────────────────────────────┘
                            │ REST API (Unix socket / TCP)
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Docker Daemon (dockerd)                    │
│                                                                   │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────────┐  │
│  │   Builder   │  │  Image Cache │  │  Container Runtime      │  │
│  │(Dockerfile) │  │  (Local)     │  │  (containerd / runc)    │  │
│  └─────────────┘  └──────────────┘  └────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                            │
               ┌────────────┴────────────┐
               ▼                         ▼
┌─────────────────────┐      ┌────────────────────────┐
│    Docker Hub        │      │   Private Registry      │
│  (public registry)   │      │  (ECR, GCR, Nexus...)   │
└─────────────────────┘      └────────────────────────┘
```

### Components

**Docker Client**
- The `docker` CLI tool you interact with.
- Sends commands to the Docker Daemon via a REST API over a Unix socket (`/var/run/docker.sock`) or TCP.
- Can communicate with a remote daemon.

**Docker Daemon (`dockerd`)**
- The background service that does the actual work.
- Manages images, containers, networks, and volumes.
- Listens for API requests from the client.

**Docker Engine**
- The combination of the Docker Client + Docker Daemon.
- The core product installed on a machine.

**Images**
- Read-only templates used to create containers.
- Built from a `Dockerfile`.
- Stored in layers.

**Containers**
- Running instances of images.
- Isolated processes with their own filesystem, network, and process space.
- Ephemeral by default (data lost when container stops).

**Registries**
- Repositories that store Docker images.
- **Docker Hub** — default public registry (`hub.docker.com`)
- **Amazon ECR** — AWS private registry
- **Google GCR / Artifact Registry** — GCP registry
- **GitHub Container Registry (GHCR)** — GitHub-hosted
- **JFrog / Nexus** — on-premise private registries

**Docker Desktop**
- GUI application for Mac and Windows that bundles Docker Engine, CLI, Compose, and a local Kubernetes cluster.
- Uses a lightweight Linux VM under the hood on non-Linux OSes.

---

## 3. Images & Containers

### What is an Image?

- A **read-only, layered template** that defines what goes into a container.
- Built by executing each instruction in a `Dockerfile`.
- Each instruction creates a new **read-only layer**.
- Identified by name and tag: `nginx:1.25`, `node:20-alpine`.

### What is a Container?

- A **running (or stopped) instance** of an image.
- Adds a thin **writable layer** on top of the image layers.
- When a container is deleted, the writable layer is deleted — the image remains unchanged.

### Image Layers

```
┌─────────────────────────────────┐  ← Writable Container Layer
├─────────────────────────────────┤  ← COPY app/ (Layer 4) [read-only]
├─────────────────────────────────┤  ← RUN npm install (Layer 3) [read-only]
├─────────────────────────────────┤  ← WORKDIR /app  (Layer 2) [read-only]
├─────────────────────────────────┤  ← FROM node:20-alpine (Layer 1) [read-only]
└─────────────────────────────────┘
```

- Each layer is cached. If a layer hasn't changed, Docker reuses the cache.
- Layers are **content-addressable** (identified by SHA256 hash).
- Multiple containers from the same image share the same read-only layers — saving disk space.

### Union File System (UnionFS)

- Merges multiple read-only layers + one writable layer into a **single unified view**.
- Implementations: `overlay2` (default), `aufs`, `devicemapper`.
- The container "sees" a normal filesystem, unaware it's composed of layers.

### Copy-on-Write (CoW)

- When a container modifies a file from the read-only image layer, the file is **copied up** to the writable layer first, then modified.
- The original image layer is never changed.
- This allows multiple containers to share the same image without conflicts.

### Container Lifecycle

```
         docker create
[Image] ──────────────► [Created]
                            │
                   docker start │ docker run
                            ▼
                        [Running] ◄─── docker restart
                            │
             docker stop ───┤─── docker kill
             (SIGTERM)      │    (SIGKILL)
                            ▼
                        [Stopped/Exited]
                            │
                   docker rm│
                            ▼
                        [Deleted]
```

| State | Description |
| --- | --- |
| Created | Container created but not started |
| Running | Process is actively running |
| Paused | Process paused (SIGSTOP) |
| Stopped/Exited | Process has terminated |
| Deleted | Container removed from system |

---

## 4. Dockerfile

A `Dockerfile` is a text file with instructions to build an image. Each instruction creates a layer.

---

### FROM

**Purpose:** Sets the base image for the build.

```docker
# Syntax
FROM <image>[:<tag>] [AS <name>]

# Examples
FROM ubuntu:22.04
FROM node:20-alpine AS builder
FROM scratch          # Empty base (for Go binaries)
```

**Best Practices:**
- Always pin a specific tag — never use `latest` in production.
- Prefer slim/alpine variants to reduce image size.
- Use `FROM scratch` for statically compiled binaries.

---

### RUN

**Purpose:** Executes a command during the build process. Creates a new layer.

```docker
# Shell form
RUN apt-get update && apt-get install -y curl

# Exec form (preferred — avoids shell interpretation)
RUN ["apt-get", "install", "-y", "curl"]

# Chain commands to minimize layers
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl git \
    && rm -rf /var/lib/apt/lists/*
```

**Best Practices:**
- Combine related `RUN` commands with `&&` to reduce layers.
- Clean up package caches in the same `RUN` step.
- Use `--no-install-recommends` for apt packages.

---

### CMD

**Purpose:** Specifies the **default command** to run when the container starts. Can be overridden at `docker run`.

```docker
# Exec form (preferred)
CMD ["node", "server.js"]

# Shell form
CMD node server.js

# As default arguments to ENTRYPOINT
CMD ["--port", "3000"]
```

**Best Practices:**
- Only one `CMD` per Dockerfile (last one wins).
- Use exec form to avoid shell wrapper overhead.
- Use `CMD` for the main process. It can be overridden by user.

---

### ENTRYPOINT

**Purpose:** Sets the **fixed executable** for the container. Arguments passed at `docker run` are appended to it.

```docker
# Exec form
ENTRYPOINT ["node", "server.js"]

# With CMD providing default args
ENTRYPOINT ["nginx", "-g"]
CMD ["daemon off;"]
```

**CMD vs ENTRYPOINT:**

| | CMD | ENTRYPOINT |
| --- | --- | --- |
| Overridable? | Yes (`docker run image mycommand`) | Only with `--entrypoint` flag |
| Purpose | Default command/args | Fixed executable |
| Combined use | Provides default args to ENTRYPOINT | Sets the binary to run |

---

### COPY

**Purpose:** Copies files/directories from the build context to the image.

```docker
# Syntax
COPY [--chown=user:group] <src> <dest>

# Examples
COPY package.json .
COPY src/ /app/src/
COPY --chown=node:node . /app
```

**Best Practices:**
- Prefer `COPY` over `ADD` for simple file copying.
- Copy `package.json` before source code to leverage layer caching for `npm install`.

---

### ADD

**Purpose:** Like `COPY` but also supports URLs and auto-extracts `.tar` archives.

```docker
ADD https://example.com/config.json /app/config.json
ADD app.tar.gz /app/     # Auto-extracts
```

**Best Practices:**
- Use `COPY` by default. Use `ADD` only for tar extraction.
- Fetching URLs with `ADD` is unpredictable — use `RUN curl` instead for control.

---

### WORKDIR

**Purpose:** Sets the working directory for subsequent instructions (`RUN`, `CMD`, `COPY`, etc.).

```docker
WORKDIR /app

# Can be used multiple times
WORKDIR /app
WORKDIR src    # Becomes /app/src
```

**Best Practices:**
- Always use `WORKDIR` instead of `RUN cd /some/dir`.
- Use absolute paths.
- Set it early in the Dockerfile.

---

### ENV

**Purpose:** Sets environment variables available at build time and in the running container.

```docker
ENV NODE_ENV=production
ENV PORT=3000 HOST=0.0.0.0

# Reference in later instructions
RUN echo $NODE_ENV
```

**Best Practices:**
- Use `ENV` for runtime configuration.
- Don't store secrets in `ENV` — use Docker secrets or external secret managers.
- Can be overridden at runtime: `docker run -e NODE_ENV=staging`

---

### ARG

**Purpose:** Defines a build-time variable. **Not available in the running container.**

```docker
ARG APP_VERSION=1.0.0
ARG BASE_IMAGE=node:20-alpine

FROM ${BASE_IMAGE}
RUN echo "Building version ${APP_VERSION}"
```

**Build:** `docker build --build-arg APP_VERSION=2.0.0 .`

**ARG vs ENV:**

| | ARG | ENV |
| --- | --- | --- |
| Scope | Build time only | Build time + runtime |
| Visible in container | No | Yes |
| Overridable | `--build-arg` | `-e` flag at runtime |

---

### EXPOSE

**Purpose:** Documents which port the container listens on. **Does NOT actually publish the port.**

```docker
EXPOSE 3000
EXPOSE 8080/tcp
EXPOSE 53/udp
```

**Best Practices:**
- Always document the port with `EXPOSE` for clarity.
- Actual port publishing is done with `-p` flag: `docker run -p 8080:3000`

---

### USER

**Purpose:** Sets the user (and optionally group) for subsequent instructions and the container process.

```docker
# Create a non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Switch to non-root user
USER appuser

CMD ["node", "server.js"]
```

**Best Practices:**
- **Always run containers as a non-root user** in production.
- Create the user in a `RUN` step before switching to it.

---

### LABEL

**Purpose:** Adds metadata to an image as key-value pairs.

```docker
LABEL maintainer="devteam@example.com"
LABEL version="1.0.0"
LABEL description="My Node.js API"
LABEL org.opencontainers.image.source="https://github.com/org/repo"
```

**Best Practices:**
- Use OCI standard labels for compatibility.
- Multiple labels can be in one `LABEL` instruction to minimize layers.

---

### HEALTHCHECK

**Purpose:** Tells Docker how to test if the container is healthy.

```docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

# Disable inherited HEALTHCHECK
HEALTHCHECK NONE
```

**Options:**
- `--interval` — How often to run the check (default: 30s)
- `--timeout` — How long to wait for a response (default: 30s)
- `--start-period` — Grace period before first check (default: 0s)
- `--retries` — Consecutive failures before marking unhealthy (default: 3)

---

### VOLUME

**Purpose:** Creates a mount point and marks it as externally mounted (volume).

```docker
VOLUME ["/data"]
VOLUME /var/log /var/db
```

**Best Practices:**
- Prefer explicit volume management with `docker run -v` or Compose.
- `VOLUME` in Dockerfile creates anonymous volumes automatically, which can be confusing.

---

### Complete Dockerfile Example

```docker
# ---- Builder Stage ----
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependency manifests first (cache layer)
COPY package*.json ./
RUN npm ci --only=production

# ---- Production Stage ----
FROM node:20-alpine AS production

# Metadata
LABEL maintainer="team@example.com" version="1.0.0"

# Security: non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy from builder
COPY --from=builder --chown=appuser:appgroup /app/node_modules ./node_modules
COPY --chown=appuser:appgroup src/ ./src/

# Runtime config
ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:3000/health || exit 1

# Switch to non-root
USER appuser

CMD ["node", "src/server.js"]
```

---

## 5. Essential Docker Commands

### Images

#### `docker build`

```bash
# Build an image from a Dockerfile
docker build -t myapp:1.0 .
docker build -t myapp:1.0 -f Dockerfile.prod .
docker build --build-arg VERSION=2.0 -t myapp:2.0 .
docker build --no-cache -t myapp:latest .   # Ignore cache
```

#### `docker pull`

```bash
# Download an image from a registry
docker pull nginx
docker pull nginx:1.25-alpine
docker pull myregistry.io/myapp:v2
```

#### `docker push`

```bash
# Upload an image to a registry
docker push myuser/myapp:1.0
docker push myregistry.io/myapp:latest
```

#### `docker tag`

```bash
# Create an alias for an image
docker tag myapp:1.0 myuser/myapp:1.0
docker tag myapp:latest myapp:backup
```

#### `docker images`

```bash
# List local images
docker images
docker images -a              # Include intermediate layers
docker images --filter "dangling=true"   # List untagged images
```

#### `docker history`

```bash
# Show layers of an image
docker history myapp:1.0
docker history --no-trunc myapp:1.0   # Full commands
```

#### `docker inspect` (image)

```bash
# Low-level details in JSON
docker inspect myapp:1.0
docker inspect --format='{{.Config.Env}}' myapp:1.0
```

#### `docker rmi`

```bash
# Remove an image
docker rmi myapp:1.0
docker rmi -f myapp:1.0                      # Force remove
docker rmi $(docker images -q)               # Remove all images
docker image prune                           # Remove dangling images
docker image prune -a                        # Remove all unused images
```

---

### Containers

#### `docker run`

The most important command — creates and starts a container.

```bash
# Basic run
docker run nginx

# Common flags
docker run -d nginx                          # Detached (background)
docker run -p 8080:80 nginx                  # Port mapping host:container
docker run -v /host/path:/container/path nginx  # Bind mount
docker run -e NODE_ENV=prod myapp            # Environment variable
docker run --name mycontainer nginx          # Named container
docker run --rm nginx                        # Remove on exit
docker run -it ubuntu bash                   # Interactive + TTY
docker run --network mynet nginx             # Custom network
docker run --memory 512m --cpus 1 myapp      # Resource limits
```

#### `docker start` / `docker stop`

```bash
docker start mycontainer       # Start a stopped container
docker stop mycontainer        # Graceful stop (SIGTERM → SIGKILL after 10s)
docker stop -t 5 mycontainer   # Custom timeout (5s)
```

#### `docker restart`

```bash
docker restart mycontainer
docker restart --time 5 mycontainer
```

#### `docker rm`

```bash
docker rm mycontainer                  # Remove stopped container
docker rm -f mycontainer               # Force remove (even if running)
docker rm $(docker ps -aq)             # Remove all stopped containers
docker container prune                 # Remove all stopped containers
```

#### `docker ps`

```bash
docker ps                   # Running containers
docker ps -a                # All containers (including stopped)
docker ps -q                # Only IDs
docker ps --filter "status=exited"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

#### `docker logs`

```bash
docker logs mycontainer
docker logs -f mycontainer          # Follow (tail -f)
docker logs --tail 100 mycontainer  # Last 100 lines
docker logs --since 1h mycontainer  # Last 1 hour
docker logs -t mycontainer          # With timestamps
```

#### `docker exec`

```bash
# Run command inside running container
docker exec mycontainer ls /app
docker exec -it mycontainer bash         # Interactive shell
docker exec -it mycontainer sh           # For Alpine (no bash)
docker exec -u root mycontainer whoami   # Run as specific user
docker exec -e VAR=value mycontainer cmd # With env var
```

#### `docker attach`

```bash
# Attach to the main process of a running container
docker attach mycontainer
# Note: Ctrl+C will stop the container. Use Ctrl+P, Ctrl+Q to detach.
# Prefer docker exec -it for interactive sessions
```

#### `docker inspect` (container)

```bash
docker inspect mycontainer
docker inspect --format='{{.NetworkSettings.IPAddress}}' mycontainer
docker inspect --format='{{.State.Status}}' mycontainer
```

#### `docker stats`

```bash
docker stats                    # Live resource usage (all containers)
docker stats mycontainer        # Specific container
docker stats --no-stream        # Single snapshot (no live update)
```

#### `docker top`

```bash
# List running processes inside container
docker top mycontainer
docker top mycontainer aux   # With ps options
```

#### `docker cp`

```bash
# Copy files between host and container
docker cp mycontainer:/app/logs/error.log ./local-error.log
docker cp ./config.json mycontainer:/app/config.json
```

---

### Volumes

#### `docker volume create`

```bash
docker volume create myvolume
docker volume create --driver local myvolume
```

#### `docker volume ls`

```bash
docker volume ls
docker volume ls --filter "dangling=true"
```

#### `docker volume inspect`

```bash
docker volume inspect myvolume
# Shows mountpoint, driver, labels
```

#### `docker volume rm`

```bash
docker volume rm myvolume
docker volume rm vol1 vol2
```

#### `docker volume prune`

```bash
docker volume prune        # Remove all unused volumes
docker volume prune -f     # Skip confirmation
```

---

### Networks

#### `docker network create`

```bash
docker network create mynetwork
docker network create --driver bridge mynetwork
docker network create --subnet 172.20.0.0/16 mynetwork
```

#### `docker network connect`

```bash
docker network connect mynetwork mycontainer
docker network connect --ip 172.20.0.10 mynetwork mycontainer
```

#### `docker network disconnect`

```bash
docker network disconnect mynetwork mycontainer
```

#### `docker network inspect`

```bash
docker network inspect mynetwork
docker network inspect bridge
```

#### `docker network ls`

```bash
docker network ls
docker network ls --filter driver=bridge
```

---

## 6. Volumes

### Why Volumes are Needed

- Container filesystems are **ephemeral** — data is lost when a container is removed.
- Multiple containers may need to share data.
- Some data (databases, logs, uploads) must persist independently of container lifecycle.

### Types of Storage

#### Bind Mounts

- Maps a **specific host path** to a container path.
- Host filesystem controls the content.
- Good for: development (live code reload), config injection.

```bash
docker run -v /home/user/myapp:/app myimage
docker run --mount type=bind,source=/home/user/myapp,target=/app myimage
```

#### Named Volumes

- Managed by Docker. Stored in `/var/lib/docker/volumes/`.
- Portable, can be backed up, inspected, and migrated.
- **Best choice for production data.**

```bash
docker volume create mydata
docker run -v mydata:/var/lib/postgresql/data postgres
docker run --mount type=volume,source=mydata,target=/var/lib/postgresql/data postgres
```

#### Anonymous Volumes

- Like named volumes but with a random ID.
- Created by `VOLUME` in Dockerfile or `-v /container/path` (no name).
- Managed by Docker but harder to reference by name.
- Cleaned up with `docker container prune` or `--rm` flag.

```bash
docker run -v /app/data myimage   # Anonymous volume
```

### When to Use Each

| Type | Use Case |
| --- | --- |
| **Bind Mount** | Dev environments, injecting config files, sharing code |
| **Named Volume** | Database data, persistent app state, production storage |
| **Anonymous Volume** | Scratch space, temp data, VOLUME declarations in images |

### `--mount` vs `-v` Flag

- `--mount` is more explicit and readable (preferred in scripts and Compose).
- `-v` is shorter and commonly used in CLI.

```bash
# Equivalent commands
docker run -v mydata:/data myimage
docker run --mount type=volume,source=mydata,target=/data myimage
```

---

## 7. Networking

### Network Drivers

#### Bridge (Default)

- **What:** Software bridge on the host. Containers on the same bridge can communicate by IP.
- **When:** Default for standalone containers. Use custom bridge for production (enables DNS by container name).

```bash
docker network create --driver bridge my-bridge
docker run --network my-bridge --name api myapi
docker run --network my-bridge --name db postgres
# "api" container can reach "db" container using hostname "db"
```

#### Host

- **What:** Container shares the host's network stack directly. No isolation.
- **When:** High-performance networking, when you don't need isolation.
- **Linux only** (not available on Mac/Windows Docker Desktop).

```bash
docker run --network host nginx
# Nginx binds directly to port 80 on the host
```

#### None

- **What:** No network interface. Completely isolated.
- **When:** Batch jobs or processes that don't need network access.

```bash
docker run --network none myapp
```

#### Overlay

- **What:** Multi-host networking. Spans multiple Docker daemons.
- **When:** Docker Swarm or multi-host container communication.

#### Custom Bridge vs Default Bridge

| Feature | Default Bridge | Custom Bridge |
| --- | --- | --- |
| DNS resolution | By IP only | By container name |
| Isolation | Shared with all containers | Isolated to network |
| On-the-fly connect | Not supported | `docker network connect` works |

**Always create custom bridge networks — never rely on the default bridge in production.**

### Port Mapping

```bash
# -p hostPort:containerPort
docker run -p 8080:80 nginx        # Host 8080 → Container 80
docker run -p 127.0.0.1:8080:80 nginx  # Bind to localhost only
docker run -p 80 nginx             # Random host port → Container 80
docker run -P nginx                # Publish all EXPOSE'd ports to random host ports
```

### DNS Inside Docker

- On a **custom bridge network**, Docker provides an embedded DNS server.
- Containers can resolve each other by **container name**.
- On the default bridge, only IP-based communication works.

```bash
# From inside "api" container on same network as "db" container:
curl http://db:5432   # "db" resolves to the database container's IP
```

---

## 8. Docker Compose

### Why Compose

- Running multi-container apps with `docker run` commands is tedious and error-prone.
- Compose defines your entire app stack in a single `compose.yml` file.
- One command to start, stop, or rebuild everything: `docker compose up`.

### `compose.yml` Structure

```yaml
version: "3.9"    # Compose file format version

services:          # Container definitions
  web:
    ...
  db:
    ...

networks:          # Custom network definitions
  mynet:
    ...

volumes:           # Named volume definitions
  mydata:
    ...
```

### Complete Multi-Container Example

```yaml
# compose.yml
version: "3.9"

services:
  # ──────────────────────────────────────────────
  # PostgreSQL Database
  # ──────────────────────────────────────────────
  db:
    image: postgres:16-alpine
    container_name: myapp-db
    restart: unless-stopped
    environment:
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: ${DB_PASSWORD}  # From .env file
      POSTGRES_DB: appdb
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - backend
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d appdb"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ──────────────────────────────────────────────
  # Redis Cache
  # ──────────────────────────────────────────────
  redis:
    image: redis:7-alpine
    container_name: myapp-redis
    restart: unless-stopped
    ports:
      - "6379:6379"
    networks:
      - backend
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3

  # ──────────────────────────────────────────────
  # Node.js API
  # ──────────────────────────────────────────────
  api:
    build:
      context: ./api
      dockerfile: Dockerfile
      target: production           # Multi-stage build target
      args:
        APP_VERSION: "1.2.0"
    container_name: myapp-api
    restart: unless-stopped
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=production
      - DATABASE_URL=postgresql://appuser:${DB_PASSWORD}@db:5432/appdb
      - REDIS_URL=redis://redis:6379
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - backend
      - frontend
    volumes:
      - ./api/logs:/app/logs
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 512M

  # ──────────────────────────────────────────────
  # Nginx Reverse Proxy
  # ──────────────────────────────────────────────
  nginx:
    image: nginx:1.25-alpine
    container_name: myapp-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/certs:/etc/nginx/certs:ro
    depends_on:
      - api
    networks:
      - frontend

# ──────────────────────────────────────────────
# Named Volumes
# ──────────────────────────────────────────────
volumes:
  pgdata:
    driver: local

# ──────────────────────────────────────────────
# Networks
# ──────────────────────────────────────────────
networks:
  backend:
    driver: bridge
  frontend:
    driver: bridge
```

### `.env` File

```bash
# .env (never commit to git)
DB_PASSWORD=supersecretpassword
```

### Key Compose Concepts

**`depends_on`**
- Controls start order. With `condition: service_healthy`, waits for health check to pass.
- Does **not** wait for the app inside to be ready — use health checks.

**`restart` Policies**

| Policy | Behavior |
| --- | --- |
| `no` | Never restart (default) |
| `always` | Always restart, even on manual stop |
| `on-failure` | Restart only on non-zero exit |
| `unless-stopped` | Restart always, except when manually stopped |

**Environment Variables in Compose**

```yaml
environment:
  # Direct value
  - NODE_ENV=production
  # From .env file (no value = look up from shell env or .env)
  - DB_PASSWORD
  # Map syntax
  NODE_ENV: production
```

### Common Compose Commands

```bash
docker compose up                  # Start all services (foreground)
docker compose up -d               # Detached (background)
docker compose up --build          # Rebuild images before starting
docker compose up api              # Start specific service only

docker compose down                # Stop and remove containers & networks
docker compose down -v             # Also remove volumes
docker compose down --rmi all      # Also remove images

docker compose ps                  # List running services
docker compose logs                # All logs
docker compose logs -f api         # Follow logs for specific service
docker compose exec api bash       # Shell into running service
docker compose run api npm test    # Run one-off command
docker compose build               # Build/rebuild images
docker compose pull                # Pull latest images
docker compose restart api         # Restart a service
docker compose scale api=3         # Scale service (legacy Compose v1)
```

---

## 9. Multi-stage Builds

### Why Use Them

- A typical build image (with compilers, dev tools, test frameworks) is **much larger** than what production needs.
- Multi-stage builds let you compile in one stage and copy **only the artifacts** to a lean production image.

### How It Works

```docker
# ── Stage 1: Build ──────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci                  # Install ALL deps (including devDependencies)
COPY . .
RUN npm run build           # Compile TypeScript, bundle, etc.
RUN npm prune --production  # Remove devDependencies

# ── Stage 2: Production ─────────────────────────────
FROM node:20-alpine AS production

# Only node_modules and built dist/ are copied
WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/package.json .

ENV NODE_ENV=production
EXPOSE 3000
CMD ["node", "dist/server.js"]
```

**Result:** Production image only contains runtime dependencies + compiled app. No TypeScript compiler, no test libraries.

### Go Example (Ultra-minimal)

```docker
# Stage 1: Build
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o server .

# Stage 2: Final — from scratch (no OS at all!)
FROM scratch
COPY --from=builder /app/server /server
ENTRYPOINT ["/server"]
```

**Result:** Final image is only the compiled Go binary — often < 20MB vs 800MB+ for the builder.

### Build a Specific Stage

```bash
docker build --target builder -t myapp:builder .    # Stop at builder stage
docker build --target production -t myapp:prod .    # Full build
```

---

## 10. Docker Best Practices

### Image Optimization

- Use **slim/alpine base images** (e.g., `node:20-alpine` vs `node:20`).
- Use **multi-stage builds** to separate build and runtime.
- Use `.dockerignore` to exclude unnecessary files from the build context.

```
# .dockerignore
node_modules
.git
*.log
.env
dist
coverage
.DS_Store
```

### Layer Caching

- Order Dockerfile instructions from **least to most frequently changing**.
- Copy dependency files first (`package.json`, `go.mod`) — install deps — then copy source.

```docker
# GOOD — Cached unless package.json changes
COPY package.json package-lock.json ./
RUN npm ci
COPY . .

# BAD — Cache busted on every code change
COPY . .
RUN npm ci
```

### Security

- Never run containers as **root** — use `USER` instruction.
- Never store secrets in `ENV`, `ARG`, or image layers.
- Regularly scan images: `docker scout cves myimage` or `trivy image myimage`.
- Sign images with Docker Content Trust (`DOCKER_CONTENT_TRUST=1`).
- Use `--read-only` flag where possible: `docker run --read-only myapp`.
- Pin base image versions with SHA: `FROM node:20-alpine@sha256:abc123...`

### Secrets

```bash
# Docker Secrets (Swarm/Compose)
echo "my-secret-password" | docker secret create db_password -
```

```yaml
# In compose.yml
services:
  db:
    secrets:
      - db_password
secrets:
  db_password:
    external: true
```

### Health Checks

- Always add `HEALTHCHECK` in production images.
- Use `/health` or `/ready` endpoints in your app.
- Kubernetes and load balancers use container health status for routing.

### Logging

- Send logs to **stdout/stderr** — let Docker and your log aggregator handle the rest.
- Don't write logs to files inside the container.
- Use `--log-driver` for centralized logging: `json-file`, `fluentd`, `awslogs`.

### Resource Limits

```bash
docker run --memory 512m --memory-swap 512m --cpus 1.0 myapp
```

- Prevent containers from starving other processes on the host.
- Always set limits in production.

---

## 11. Troubleshooting Docker

### Port Already in Use

```
Error: bind: address already in use
```

**Cause:** Another process (or container) is using the host port.

```bash
# Find what's using the port
lsof -i :8080
netstat -tuln | grep 8080
# Kill the process or change the host port
docker run -p 8081:80 nginx
```

### Permission Denied

```
Got permission denied while trying to connect to Docker daemon socket
```

**Cause:** Current user is not in the `docker` group.

```bash
sudo usermod -aG docker $USER
# Log out and back in, or run:
newgrp docker
```

### Container Exits Immediately

**Cause:** The main process exited (either crashed or completed).

```bash
docker logs mycontainer           # Check the last output
docker run -it myimage bash       # Override CMD to debug interactively
docker ps -a                      # Check exit code in STATUS column
```

Common causes:
- App crashed due to missing env var / config
- CMD command not found (typo or wrong path)
- `CMD ["bash"]` — bash exits if no stdin is attached (use `-it`)

### Volume Mounting Issues

- File not found: check that the host path exists.
- Permission denied: file created by root in container, readable by user on host.

```bash
docker run -v $(pwd)/data:/app/data myimage   # Use absolute paths
# Fix permissions
docker exec mycontainer chown -R appuser:appgroup /app/data
```

### Network Issues (Container Can't Reach Another)

```bash
# Check containers are on the same network
docker inspect mycontainer | grep NetworkMode
docker network inspect mynetwork

# Ping from inside container
docker exec mycontainer ping db
docker exec mycontainer nslookup db    # DNS check
```

- Use custom bridge networks (not default) for DNS resolution.
- Check firewall rules on the host.

### Disk Space Problems

```
no space left on device
```

```bash
docker system df           # Show disk usage
docker system prune        # Remove stopped containers, unused networks, dangling images
docker system prune -a     # Also remove unused images
docker volume prune        # Remove unused volumes
```

---

# Part 2: Kubernetes

---

## 12. Introduction to Kubernetes

### What is Kubernetes?

- An open-source **container orchestration platform** originally created by Google (donated to CNCF).
- Automates deployment, scaling, load balancing, healing, and rollout of containerized applications.
- Often abbreviated as **K8s** (K + 8 letters + s).

### Why Kubernetes is Needed

- Docker runs containers on a **single host**. What happens if:
  - The host goes down?
  - Traffic spikes and you need more instances?
  - A container crashes and needs restart?
  - You need zero-downtime deployments?
- Kubernetes solves all of this across **a cluster of machines**.

| Challenge | Kubernetes Solution |
| --- | --- |
| Container crashes | Auto-restarts via ReplicaSet |
| Traffic spikes | Horizontal Pod Autoscaler |
| Host failure | Reschedules pods on healthy nodes |
| Zero-downtime deploy | Rolling updates |
| Service discovery | DNS-based service discovery |
| Load balancing | Built-in via Services |
| Config management | ConfigMaps and Secrets |

### Docker vs Kubernetes

| Aspect | Docker | Kubernetes |
| --- | --- | --- |
| Scope | Single host | Cluster of machines |
| Orchestration | Basic (Compose/Swarm) | Full-featured |
| Scaling | Manual | Auto (HPA) |
| Self-healing | No | Yes |
| Load balancing | Manual | Built-in |
| Service discovery | DNS within Compose | DNS within cluster |
| Config management | Env vars / Secrets | ConfigMaps / Secrets |
| Use case | Local dev, simple deployments | Production at scale |

**Relationship:** Kubernetes uses Docker (or containerd/CRI-O) as its container runtime. You build Docker images, push to a registry, and Kubernetes runs those images.

---

## 13. Kubernetes Architecture

### Architecture Diagram

```
┌─────────────────────────── KUBERNETES CLUSTER ─────────────────────────────┐
│                                                                              │
│  ┌────────────────────── CONTROL PLANE ──────────────────────┐              │
│  │                                                            │              │
│  │  ┌────────────┐  ┌──────────────┐  ┌──────────────────┐  │              │
│  │  │ API Server │  │  Scheduler   │  │Controller Manager│  │              │
│  │  │(kube-       │  │(kube-        │  │(kube-controller- │  │              │
│  │  │ apiserver)  │  │ scheduler)   │  │  manager)        │  │              │
│  │  └─────┬──────┘  └──────┬───────┘  └────────┬─────────┘  │              │
│  │        │                │                   │             │              │
│  │        └────────────────┼───────────────────┘             │              │
│  │                         │                                 │              │
│  │                  ┌──────▼──────┐                          │              │
│  │                  │    etcd     │                          │              │
│  │                  │ (key-value  │                          │              │
│  │                  │   store)    │                          │              │
│  │                  └─────────────┘                          │              │
│  └────────────────────────────────────────────────────────────┘              │
│                                                                              │
│  ┌─────────────── WORKER NODE 1 ──────────────┐                             │
│  │                                             │                             │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  │                             │
│  │  │  Pod     │  │  Pod     │  │  Pod     │  │                             │
│  │  │ [App A]  │  │ [App B]  │  │ [App A]  │  │                             │
│  │  └──────────┘  └──────────┘  └──────────┘  │                             │
│  │                                             │                             │
│  │  ┌──────────┐  ┌───────────┐               │                             │
│  │  │ kubelet  │  │kube-proxy │               │                             │
│  │  └──────────┘  └───────────┘               │                             │
│  │  Container Runtime (containerd)             │                             │
│  └─────────────────────────────────────────────┘                             │
│                                                                              │
│  ┌─────────────── WORKER NODE 2 ──────────────┐                             │
│  │  (same structure)                           │                             │
│  └─────────────────────────────────────────────┘                             │
└──────────────────────────────────────────────────────────────────────────────┘
        │
   kubectl (CLI) / REST API clients communicate with API Server
```

### Control Plane Components

#### API Server (`kube-apiserver`)

- The **single entry point** for all cluster operations.
- Exposes the Kubernetes REST API.
- Validates and processes all requests (from kubectl, controllers, kubelet).
- Writes cluster state to etcd.

#### Scheduler (`kube-scheduler`)

- Watches for newly created pods with no assigned node.
- Selects the best node based on: resource requests, node affinity, taints/tolerations, and current load.
- Does NOT start the pod — kubelet does.

#### Controller Manager (`kube-controller-manager`)

- Runs a collection of **control loops** (controllers) to maintain desired state.
- Examples:
  - **ReplicaSet Controller** — ensures the right number of pod replicas are running.
  - **Node Controller** — monitors node health.
  - **Deployment Controller** — manages rolling updates.
  - **Job Controller** — ensures Jobs complete.

#### etcd

- A **distributed key-value store** — the cluster's source of truth.
- Stores all cluster state: pods, services, configs, secrets, nodes.
- All API server writes go to etcd.
- **Must be backed up** in production — losing etcd means losing the cluster state.

### Worker Node Components

#### kubelet

- An agent running on **every worker node**.
- Receives PodSpecs from the API server and ensures the described containers are running and healthy.
- Reports pod status back to the API server.
- Does NOT manage containers not created by Kubernetes.

#### kube-proxy

- Runs on every node. Maintains network rules.
- Implements the Kubernetes **Service** concept by configuring iptables/ipvs rules.
- Routes traffic to the correct pod(s) for a Service.

#### Container Runtime

- The software that actually runs containers.
- Kubernetes talks to it via **CRI** (Container Runtime Interface).
- Options: **containerd** (most common), **CRI-O**, Docker Engine (via shim, legacy).

---

## 14. Core Kubernetes Objects

### Pod

**Purpose:** The smallest deployable unit in Kubernetes. Wraps one or more containers that share network and storage.

**When to use:** Almost never create Pods directly — use Deployments. Direct Pods are not rescheduled if they die.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
  labels:
    app: my-app
spec:
  containers:
    - name: app
      image: myapp:1.0
      ports:
        - containerPort: 3000
      resources:
        requests:
          memory: "64Mi"
          cpu: "250m"
        limits:
          memory: "128Mi"
          cpu: "500m"
      env:
        - name: NODE_ENV
          value: "production"
```

**Key facts:**
- All containers in a Pod share the same IP address and port space.
- Containers in a Pod can communicate via `localhost`.
- Pods are ephemeral — if a Pod dies, it's gone unless managed by a controller.

---

### ReplicaSet

**Purpose:** Ensures a specified number of pod replicas are running at all times.

**When to use:** Rarely directly — Deployments manage ReplicaSets for you.

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: my-replicaset
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
        - name: app
          image: myapp:1.0
```

---

### Deployment

**Purpose:** Manages ReplicaSets and provides declarative updates. Enables rolling updates, rollbacks, and scaling.

**When to use:** For all stateless applications — this is the most commonly used object.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
  namespace: production
  labels:
    app: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1          # Max pods above desired count during update
      maxUnavailable: 0    # Keep all pods available during update
  template:
    metadata:
      labels:
        app: my-app
        version: "1.0"
    spec:
      containers:
        - name: app
          image: myapp:1.0
          ports:
            - containerPort: 3000
          readinessProbe:
            httpGet:
              path: /health
              port: 3000
            initialDelaySeconds: 5
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /health
              port: 3000
            initialDelaySeconds: 15
            periodSeconds: 20
```

---

### Service

**Purpose:** Provides a stable DNS name and IP to access a set of pods. Load balances traffic across matching pods.

**When to use:** Every time you need to access pods from inside or outside the cluster.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: ClusterIP         # ClusterIP | NodePort | LoadBalancer
  selector:
    app: my-app           # Targets pods with this label
  ports:
    - protocol: TCP
      port: 80             # Service port (what clients use)
      targetPort: 3000     # Container port (where app listens)
```

---

### Namespace

**Purpose:** Virtual clusters within a physical cluster. Logical isolation of resources.

**When to use:** Separate environments (dev/staging/prod), teams, or projects.

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: production
  labels:
    env: prod
```

```bash
# Common namespaces
kubectl get namespaces
# default       — Default namespace for objects with no namespace specified
# kube-system   — Kubernetes system components
# kube-public   — Publicly readable data
# kube-node-lease — Node heartbeat leases
```

---

### ConfigMap

**Purpose:** Stores non-sensitive configuration data as key-value pairs. Decouples config from container images.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  LOG_LEVEL: "info"
  APP_PORT: "3000"
  config.yaml: |
    database:
      host: db-service
      port: 5432
```

**Using ConfigMap in a Pod:**

```yaml
# As environment variables
envFrom:
  - configMapRef:
      name: app-config

# As a specific env var
env:
  - name: LOG_LEVEL
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: LOG_LEVEL

# As a mounted file
volumes:
  - name: config-volume
    configMap:
      name: app-config
volumeMounts:
  - name: config-volume
    mountPath: /etc/config
```

---

### Secret

**Purpose:** Stores sensitive data (passwords, tokens, keys). Values are base64-encoded (not encrypted by default — enable encryption at rest in production).

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  # base64-encoded: echo -n "mypassword" | base64
  DB_PASSWORD: bXlwYXNzd29yZA==
  DB_USERNAME: YXBwdXNlcg==
```

```bash
# Create from CLI
kubectl create secret generic db-secret \
  --from-literal=DB_PASSWORD=mypassword \
  --from-literal=DB_USERNAME=appuser
```

**Using in a Pod:**

```yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: DB_PASSWORD
```

---

### Job

**Purpose:** Runs a task to **completion**. Unlike Deployments, Jobs are not persistent — they terminate when the task is done.

**When to use:** Database migrations, batch data processing, one-off scripts.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: db-migration
spec:
  completions: 1        # Number of successful completions required
  parallelism: 1        # Number of pods running in parallel
  backoffLimit: 3       # Retry limit on failure
  template:
    spec:
      restartPolicy: Never    # OnFailure | Never (required for Jobs)
      containers:
        - name: migrate
          image: myapp:1.0
          command: ["node", "scripts/migrate.js"]
          env:
            - name: DB_URL
              valueFrom:
                secretKeyRef:
                  name: db-secret
                  key: DB_URL
```

---

### CronJob

**Purpose:** Runs Jobs on a scheduled (cron) basis.

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: backup-job
spec:
  schedule: "0 2 * * *"     # Daily at 2 AM (standard cron syntax)
  concurrencyPolicy: Forbid  # Allow | Forbid | Replace
  successfulJobsHistoryLimit: 3
  failedJobsHistoryLimit: 1
  jobTemplate:
    spec:
      template:
        spec:
          restartPolicy: OnFailure
          containers:
            - name: backup
              image: backup-tool:1.0
              command: ["/backup.sh"]
```

---

### DaemonSet

**Purpose:** Ensures a copy of a pod runs on **every node** (or a selected subset).

**When to use:** Log collectors (Fluentd), node monitoring (Prometheus node-exporter), network plugins.

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: log-collector
spec:
  selector:
    matchLabels:
      app: log-collector
  template:
    metadata:
      labels:
        app: log-collector
    spec:
      containers:
        - name: fluentd
          image: fluentd:latest
          volumeMounts:
            - name: varlog
              mountPath: /var/log
      volumes:
        - name: varlog
          hostPath:
            path: /var/log
```

---

### StatefulSet

**Purpose:** Like a Deployment, but for **stateful applications**. Provides stable network identities and persistent storage per pod.

**When to use:** Databases (PostgreSQL, MySQL, MongoDB, Kafka, Zookeeper).

**Key differences from Deployment:**
- Pods have stable, predictable names: `db-0`, `db-1`, `db-2`.
- Pods start and stop in order (sequential).
- Each pod gets its own PersistentVolumeClaim.

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: postgres      # Must reference a Headless Service
  replicas: 3
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:16
          ports:
            - containerPort: 5432
          env:
            - name: POSTGRES_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: postgres-secret
                  key: password
          volumeMounts:
            - name: postgres-data
              mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
    - metadata:
        name: postgres-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 10Gi
```

---

### Ingress

**Purpose:** HTTP/HTTPS routing rules for external traffic. Routes requests to different Services based on hostname or URL path. Requires an **Ingress Controller** (nginx, traefik, etc.).

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.example.com
      secretName: api-tls-cert
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: api-service
                port:
                  number: 80
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend-service
                port:
                  number: 80
```

---

### Persistent Volume (PV)

**Purpose:** A piece of storage provisioned in the cluster, independent of pod lifecycle. Cluster-level resource.

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce         # RWO: one node | RWX: many nodes | ROX: many nodes read
  persistentVolumeReclaimPolicy: Retain  # Retain | Delete | Recycle
  storageClassName: standard
  hostPath:
    path: /mnt/data         # For local dev/testing
```

---

### Persistent Volume Claim (PVC)

**Purpose:** A user's request for storage. Binds to an available PV that satisfies the requirements.

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
  storageClassName: standard
```

**Using PVC in a Pod:**

```yaml
volumes:
  - name: storage
    persistentVolumeClaim:
      claimName: my-pvc
containers:
  - name: app
    volumeMounts:
      - mountPath: /data
        name: storage
```

---

## 15. Kubernetes Networking

### Service Types

#### ClusterIP (Default)

- Exposes service on an **internal cluster IP**.
- Only reachable from within the cluster.
- **Use:** Internal microservice-to-microservice communication.

```yaml
spec:
  type: ClusterIP
  selector:
    app: my-api
  ports:
    - port: 80
      targetPort: 3000
```

#### NodePort

- Exposes service on a **static port on each node's IP** (range: 30000–32767).
- Reachable from outside the cluster at `<NodeIP>:<NodePort>`.
- **Use:** Dev/testing environments, non-production external access.

```yaml
spec:
  type: NodePort
  selector:
    app: my-api
  ports:
    - port: 80
      targetPort: 3000
      nodePort: 30080    # Optional: auto-assigned if omitted
```

#### LoadBalancer

- Provisions a **cloud provider load balancer** (AWS ELB, GCP LB, Azure LB).
- Gets an external IP that routes to the NodePort.
- **Use:** Production external traffic entry point.

```yaml
spec:
  type: LoadBalancer
  selector:
    app: my-api
  ports:
    - port: 80
      targetPort: 3000
```

#### ExternalName

- Maps a Service to an **external DNS name**.
- No proxying — returns a CNAME record.
- **Use:** Integrating external services (RDS, third-party APIs) using in-cluster DNS.

```yaml
spec:
  type: ExternalName
  externalName: mydb.us-east-1.rds.amazonaws.com
```

### Ingress

- HTTP/HTTPS layer 7 routing (see Ingress object above).
- Single entry point for multiple services based on host/path routing.
- More cost-effective than one LoadBalancer per service.

### Service Discovery

- Kubernetes automatically creates DNS entries for all Services.
- Format: `<service-name>.<namespace>.svc.cluster.local`
- From within the same namespace: just use `<service-name>`.
- From a different namespace: use `<service-name>.<namespace>`.

```bash
# From inside a pod in the "default" namespace:
curl http://api-service          # Same namespace
curl http://api-service.production.svc.cluster.local  # Cross-namespace
```

---

## 16. Storage

### Persistent Volume Lifecycle

```
[Admin creates PV]  →  [Developer creates PVC]  →  [PVC binds to PV]
                                                          │
                                              [Pod mounts PVC as volume]
                                                          │
                                              [Pod uses /data as storage]
                                                          │
                                              [Pod deleted — PV still exists]
                                                          │
                                              [Based on reclaimPolicy]
                                              Retain → PV available, data kept
                                              Delete → PV and data deleted
```

### Storage Classes

Enables **dynamic provisioning** — PVs are automatically created when a PVC is submitted.

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  encrypted: "true"
reclaimPolicy: Delete
volumeBindingMode: WaitForFirstConsumer   # Provisions only when pod is scheduled
```

### Access Modes

| Mode | Description |
| --- | --- |
| `ReadWriteOnce` (RWO) | Single node read/write |
| `ReadOnlyMany` (ROX) | Many nodes read-only |
| `ReadWriteMany` (RWX) | Many nodes read/write (needs NFS/CephFS) |

---

## 17. Scaling & Updates

### Rolling Updates

The default Deployment update strategy. Gradually replaces old pods with new ones.

```bash
# Update image (triggers rolling update)
kubectl set image deployment/my-app app=myapp:2.0

# Monitor the rollout
kubectl rollout status deployment/my-app

# View rollout history
kubectl rollout history deployment/my-app
```

**Process:**

```
[v1] [v1] [v1] [v1]     — Initial state (replicas: 4)
[v1] [v1] [v1] [v2]     — maxSurge=1, maxUnavailable=0: add one v2
[v1] [v1] [v2] [v2]     — Remove one v1, add another v2
[v1] [v2] [v2] [v2]     — Continue...
[v2] [v2] [v2] [v2]     — Complete
```

### Rollbacks

```bash
kubectl rollout undo deployment/my-app                    # Roll back to previous version
kubectl rollout undo deployment/my-app --to-revision=2   # Roll back to specific revision
```

### Horizontal Pod Autoscaler (HPA)

Automatically scales the number of pods based on CPU/memory utilization or custom metrics.

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: my-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: my-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70   # Scale when avg CPU > 70%
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
```

```bash
kubectl apply -f hpa.yaml
kubectl get hpa
kubectl describe hpa my-hpa
```

**Note:** HPA requires `metrics-server` to be installed in the cluster.

### Manual Scaling

```bash
kubectl scale deployment my-app --replicas=5
kubectl scale deployment my-app --replicas=0    # Scale to zero (stop all pods)
```

---

## 18. Kubernetes YAML

### YAML Structure — Required Fields

Every Kubernetes resource YAML has these four top-level fields:

```yaml
apiVersion: apps/v1         # API group + version
kind: Deployment            # Resource type
metadata:                   # Identifying information
  name: my-deployment
  namespace: default
  labels:
    app: my-app
    env: production
  annotations:
    description: "Main API deployment"
spec:                       # Desired state (varies by kind)
  ...
```

### `apiVersion`

| Resource | apiVersion |
| --- | --- |
| Pod, Service, ConfigMap, Secret, PV, PVC, Namespace | `v1` |
| Deployment, ReplicaSet, StatefulSet, DaemonSet | `apps/v1` |
| Job, CronJob | `batch/v1` |
| HorizontalPodAutoscaler | `autoscaling/v2` |
| Ingress | `networking.k8s.io/v1` |
| NetworkPolicy | `networking.k8s.io/v1` |
| StorageClass | `storage.k8s.io/v1` |

### Labels vs Annotations

|  | Labels | Annotations |
| --- | --- | --- |
| Purpose | Identify and select objects | Attach non-identifying metadata |
| Used for | Selectors, grouping, filtering | Tooling, descriptions, configs |
| Query with | `kubectl get pods -l app=my-app` | Cannot select |
| Examples | `app`, `env`, `version`, `tier` | `git-commit`, `managed-by`, `description` |

### Selectors

```yaml
# matchLabels — equality based
selector:
  matchLabels:
    app: my-app
    env: production

# matchExpressions — set based
selector:
  matchExpressions:
    - key: env
      operator: In              # In | NotIn | Exists | DoesNotExist
      values: ["production", "staging"]
```

### Complete Deployment Manifest Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-deployment
  namespace: production
  labels:
    app: api
    team: backend
  annotations:
    deployment.kubernetes.io/revision: "1"
    git-commit: "abc123"
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: api
        version: "1.2.0"
    spec:
      serviceAccountName: api-sa
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
      containers:
        - name: api
          image: myregistry.io/api:1.2.0
          imagePullPolicy: Always
          ports:
            - name: http
              containerPort: 3000
          env:
            - name: NODE_ENV
              value: production
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-secret
                  key: password
          envFrom:
            - configMapRef:
                name: app-config
          resources:
            requests:
              cpu: "250m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
          readinessProbe:
            httpGet:
              path: /health/ready
              port: 3000
            initialDelaySeconds: 10
            periodSeconds: 5
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /health/live
              port: 3000
            initialDelaySeconds: 20
            periodSeconds: 15
            failureThreshold: 3
          volumeMounts:
            - name: logs
              mountPath: /app/logs
      volumes:
        - name: logs
          emptyDir: {}
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchLabels:
                    app: api
                topologyKey: kubernetes.io/hostname
```

---

## 19. Essential kubectl Commands

### Cluster

```bash
# Cluster information
kubectl cluster-info
kubectl cluster-info dump            # Detailed diagnostics

# Version
kubectl version
kubectl version --client

# Context/config management
kubectl config view                  # Show kubeconfig
kubectl config get-contexts          # List all contexts
kubectl config current-context       # Show active context
kubectl config use-context prod-ctx  # Switch context
kubectl config set-context --current --namespace=production  # Set default namespace
```

---

### Pods

```bash
# List pods
kubectl get pods
kubectl get pods -n production
kubectl get pods -A                         # All namespaces
kubectl get pods -l app=my-app              # By label
kubectl get pods -o wide                    # With node info
kubectl get pods --watch                    # Live updates
kubectl get pod my-pod -o yaml              # Full YAML

# Pod details
kubectl describe pod my-pod
kubectl describe pod my-pod -n production

# Logs
kubectl logs my-pod
kubectl logs my-pod -c container-name       # Multi-container pod
kubectl logs -f my-pod                      # Stream logs
kubectl logs --tail=100 my-pod              # Last 100 lines
kubectl logs --since=1h my-pod             # Last 1 hour
kubectl logs my-pod --previous             # Previous terminated container

# Execute into pod
kubectl exec -it my-pod -- bash
kubectl exec -it my-pod -c container -- sh  # Specific container
kubectl exec my-pod -- ls /app              # Non-interactive command

# Delete pod
kubectl delete pod my-pod
kubectl delete pod my-pod --grace-period=0 --force   # Immediate

# Copy files
kubectl cp my-pod:/app/logs/error.log ./error.log
kubectl cp ./config.yaml my-pod:/app/config.yaml
```

---

### Deployments

```bash
# Create/Apply
kubectl create deployment my-app --image=myapp:1.0
kubectl apply -f deployment.yaml
kubectl apply -f ./k8s/               # Apply all YAML in a directory

# View
kubectl get deployments
kubectl get deploy -n production
kubectl describe deployment my-app

# Rollout management
kubectl rollout status deployment/my-app
kubectl rollout history deployment/my-app
kubectl rollout history deployment/my-app --revision=2
kubectl rollout undo deployment/my-app
kubectl rollout undo deployment/my-app --to-revision=2
kubectl rollout restart deployment/my-app   # Force rolling restart

# Scaling
kubectl scale deployment my-app --replicas=5

# Update image
kubectl set image deployment/my-app app=myapp:2.0

# Delete
kubectl delete deployment my-app
kubectl delete -f deployment.yaml
```

---

### Services

```bash
# Expose a deployment as a service
kubectl expose deployment my-app --type=ClusterIP --port=80 --target-port=3000
kubectl expose deployment my-app --type=LoadBalancer --port=80

# View services
kubectl get services
kubectl get svc -n production
kubectl describe service my-service

# Delete
kubectl delete service my-service
```

---

### Debugging

```bash
# Events (great for troubleshooting)
kubectl get events
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl get events -n production --watch

# Resource usage (requires metrics-server)
kubectl top nodes
kubectl top pods
kubectl top pods -n production

# Port forwarding (local dev access)
kubectl port-forward pod/my-pod 8080:3000
kubectl port-forward service/my-service 8080:80
kubectl port-forward deployment/my-app 8080:3000

# General
kubectl get all                          # Get all resources in namespace
kubectl get all -n production
kubectl api-resources                    # List all resource types
kubectl explain deployment.spec          # Built-in documentation
kubectl diff -f deployment.yaml          # Preview changes before applying

# Run a temporary debug pod
kubectl run debug --image=busybox --rm -it -- sh
kubectl run debug --image=nicolaka/netshoot --rm -it -- bash
```

---

## 20. Docker + Kubernetes Workflow

### Complete Deployment Lifecycle

```
Developer Machine
     │
     │  1. Write code
     │
     ▼
 git push ──────────────────────────────────────────────────────┐
                                                                │
                                                     CI/CD Pipeline (GitHub Actions / Jenkins / GitLab CI)
                                                                │
     ┌──────────────────────────────────────────────────────────┘
     │
     │  2. Build Docker Image
     │     docker build -t myregistry.io/myapp:v1.2.0 .
     │
     ▼
     │  3. Run Tests
     │     docker run myapp:v1.2.0 npm test
     │
     ▼
     │  4. Push Image to Registry
     │     docker push myregistry.io/myapp:v1.2.0
     │
     ▼
Container Registry (ECR / GCR / GHCR / Docker Hub)
     │
     │  5. Update Kubernetes Manifest
     │     kubectl set image deployment/myapp app=myregistry.io/myapp:v1.2.0
     │     OR: Update YAML and kubectl apply -f deployment.yaml
     │
     ▼
Kubernetes Cluster
     │
     │  6. Scheduler assigns pods to nodes
     │     Nodes pull image from registry
     │     kubelet starts containers
     │
     ▼
     │  7. Health checks pass
     │     Readiness probe → pod added to Service endpoint
     │     Old pods terminated (rolling update complete)
     │
     ▼
     │  8. Traffic flows:
     │     Internet → LoadBalancer → NodePort → Service → Pod
     │     OR
     │     Internet → Ingress → ClusterIP Service → Pod
     │
     ▼
Running Application ✓
```

### Traffic Flow Diagram

```
Internet
    │
    ▼
[DNS: api.example.com]
    │
    ▼
[Cloud LoadBalancer]
    │
    ▼
[Ingress Controller (nginx)]
    │
    ├─── /api/* ──────► [api-service:ClusterIP:80] ──► [api-pod-1]
    │                                               └─► [api-pod-2]
    │                                               └─► [api-pod-3]
    │
    └─── /* ──────────► [frontend-service:ClusterIP:80] ──► [frontend-pod]
```

---

## 21. CI/CD Integration

### Docker in CI/CD

```yaml
# Example: GitHub Actions workflow
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      # 1. Log in to registry
      - name: Log in to ECR
        uses: aws-actions/amazon-ecr-login@v1

      # 2. Build and tag image
      - name: Build image
        run: |
          IMAGE_TAG=${{ github.sha }}
          docker build -t $ECR_REGISTRY/myapp:$IMAGE_TAG .
          docker tag $ECR_REGISTRY/myapp:$IMAGE_TAG $ECR_REGISTRY/myapp:latest

      # 3. Push image
      - name: Push image
        run: |
          docker push $ECR_REGISTRY/myapp:$IMAGE_TAG
          docker push $ECR_REGISTRY/myapp:latest

      # 4. Deploy to Kubernetes
      - name: Deploy to K8s
        run: |
          kubectl set image deployment/myapp \
            app=$ECR_REGISTRY/myapp:$IMAGE_TAG \
            -n production
          kubectl rollout status deployment/myapp -n production
```

### Key CI/CD Practices

- **Tag images with git SHA** — never overwrite `latest` in production.
- **Scan images** before pushing: `trivy image myapp:v1` or `docker scout`.
- **Use `kubectl rollout status`** to wait for deployment to complete in CI — fail the pipeline if rollout fails.
- **Auto-rollback:** If `kubectl rollout status` fails, trigger `kubectl rollout undo`.
- **Separate image build pipeline from deployment pipeline** for security.
- **Use GitOps** (ArgoCD, Flux) to sync cluster state from a Git repo — more reliable than direct `kubectl apply`.

---

## 22. Best Practices

### Resource Requests & Limits

```yaml
resources:
  requests:              # Guaranteed minimum (used for scheduling)
    cpu: "250m"          # 250 millicores = 0.25 CPU
    memory: "256Mi"
  limits:                # Maximum allowed (throttled/OOMKilled if exceeded)
    cpu: "500m"
    memory: "512Mi"
```

- **Always set both** requests and limits.
- Limits without requests — requests default to limits.
- Without limits — a runaway pod can starve the entire node.
- CPU is **throttled** at limit. Memory limit triggers **OOMKill**.
- Use `kubectl top pods` + monitoring (Prometheus/Grafana) to right-size resources.

### Health Probes

```yaml
# Readiness Probe — "Am I ready to receive traffic?"
# Pod is removed from Service endpoints if it fails.
readinessProbe:
  httpGet:
    path: /health/ready
    port: 3000
  initialDelaySeconds: 10    # Wait before first check
  periodSeconds: 5           # Check every 5s
  failureThreshold: 3        # 3 failures → not ready

# Liveness Probe — "Am I still alive / not deadlocked?"
# Pod is restarted if it fails.
livenessProbe:
  httpGet:
    path: /health/live
    port: 3000
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3

# Startup Probe — "Have I finished starting up?"
# Disables liveness/readiness until startup succeeds.
# Use for slow-starting apps (JVM, etc.)
startupProbe:
  httpGet:
    path: /health/live
    port: 3000
  failureThreshold: 30       # Up to 30 * 10s = 5 min to start
  periodSeconds: 10
```

### Liveness vs Readiness

|  | Readiness | Liveness |
| --- | --- | --- |
| Question | "Ready for traffic?" | "Still functioning?" |
| Action on failure | Remove from Service | Restart container |
| Use for | Startup complete, dependencies ready | Deadlocks, hung processes |

### Secrets Management

- Don't put secrets in YAML files committed to git.
- Use external secret stores: **AWS Secrets Manager**, **HashiCorp Vault**, **Sealed Secrets**.
- Enable **encryption at rest** for etcd secrets in production.
- Rotate secrets regularly.

### Namespace Organization

```
cluster/
├── kube-system/        # K8s internals
├── monitoring/         # Prometheus, Grafana
├── ingress-nginx/      # Ingress controller
├── production/         # Production workloads
├── staging/            # Staging workloads
└── development/        # Dev workloads
```

### Image Versioning

- Never use `latest` in production — use semantic versioning or git SHA.
- Use `imagePullPolicy: Always` when using mutable tags.
- Use `imagePullPolicy: IfNotPresent` for immutable tags (faster, more reliable).

### Logging & Monitoring

- Use stdout/stderr in containers — collected by node-level logging agents.
- **EFK Stack:** Elasticsearch + Fluentd + Kibana.
- **PLG Stack:** Prometheus + Loki + Grafana.
- Set up alerts for pod crash loops, high CPU/memory, deployment failures.

---

## 23. Common Interview Questions

### Docker Fundamentals

**Q1: What is the difference between a Docker image and a container?**
> An image is a read-only, layered template. A container is a running (or stopped) instance of that image with a writable layer on top.

**Q2: What is the difference between `CMD` and `ENTRYPOINT`?**
> `ENTRYPOINT` sets the fixed executable — it can't be overridden without `--entrypoint`. `CMD` provides default arguments that CAN be overridden at `docker run`. When both are used, `CMD` acts as default args to `ENTRYPOINT`.

**Q3: What is Docker's Union File System?**
> A mechanism that merges multiple read-only image layers plus one writable container layer into a single unified filesystem view. `overlay2` is the most common implementation.

**Q4: What is copy-on-write in Docker?**
> When a container modifies a file from the image's read-only layers, Docker copies that file to the container's writable layer first, then modifies the copy. The original image layer is never changed.

**Q5: What is a dangling image?**
> An image with no tag and no associated container — typically the result of a rebuild. Listed by `docker images -f dangling=true`. Removed with `docker image prune`.

**Q6: What does `docker run --rm` do?**
> Automatically removes the container when it exits. Useful for one-off commands and CI builds where you don't want leftover stopped containers.

**Q7: What is the difference between `COPY` and `ADD`?**
> `COPY` simply copies files. `ADD` additionally supports auto-extracting tar archives and fetching URLs. Best practice is to use `COPY` unless you specifically need `ADD`'s features.

**Q8: What is a multi-stage build?**
> A Dockerfile with multiple `FROM` statements, where later stages can copy artifacts from earlier stages. Allows building in a large image and copying only the final binary/dist to a small production image.

**Q9: What does `.dockerignore` do?**
> Specifies files/directories to exclude from the build context. Reduces build context size, speeds up builds, prevents accidentally copying secrets or `node_modules` into the image.

**Q10: How does Docker layer caching work?**
> Docker checks if each instruction and its inputs have changed since the last build. If not, it reuses the cached layer. Cache is invalidated for a layer and all subsequent layers when: the instruction changes, the files it copies change, or `--no-cache` is specified.

---

### Docker Compose

**Q11: What is Docker Compose and when would you use it?**
> A tool for defining and running multi-container applications using a YAML file. Used for local development, testing environments, and simple production setups where you need multiple services (app + db + cache) to work together.

**Q12: What is the difference between `docker compose up` and `docker compose run`?**
> `up` starts all defined services. `run` starts a specific service with an optional one-off command (e.g., `docker compose run api npm test`) and doesn't start dependent services unless explicitly needed.

**Q13: What does `depends_on` do in Docker Compose?**
> Controls the start order. Without a `condition`, it only waits for the container to start — not for the service inside to be ready. Use `condition: service_healthy` (requires a health check) to wait for readiness.

**Q14: How do you pass environment variables in Compose?**
> Three ways: inline in `environment:`, reference from shell/`.env` file by listing variable name without value, or use `env_file:` to specify a file.

**Q15: What is the difference between `restart: always` and `restart: unless-stopped`?**
> `always` restarts the container even after `docker stop`. `unless-stopped` restarts automatically but respects an explicit manual stop.

---

### Volumes & Networking

**Q16: What are the three types of Docker volumes?**
> Named volumes (managed by Docker, identified by name), bind mounts (specific host path mapped to container path), anonymous volumes (managed by Docker, random ID, harder to reference).

**Q17: Why should you prefer named volumes over bind mounts in production?**
> Named volumes are managed by Docker, portable, can be easily backed up and inspected, and work consistently across different OSes. Bind mounts depend on host path structure.

**Q18: What is the default Docker network?**
> The default `bridge` network. However, containers on the default bridge can only communicate by IP — no DNS resolution. Custom bridge networks should be used for production, as they support DNS by container name.

**Q19: How does Docker DNS work?**
> On custom bridge networks, Docker runs an embedded DNS server. Containers on the same network can resolve each other by container name. The default bridge network does not support this.

**Q20: What is the difference between `-p 8080:80` and `-p 80`?**
> `-p 8080:80` maps host port 8080 to container port 80. `-p 80` maps container port 80 to a random available host port.

---

### Kubernetes Architecture

**Q21: What is the role of the API Server in Kubernetes?**
> The single entry point for all cluster operations. All components (kubectl, kubelet, controllers) communicate only through the API server. It validates requests and persists state to etcd.

**Q22: What does etcd store?**
> All cluster state: pod definitions, service configs, secrets, configmaps, node status, RBAC policies. It's the single source of truth — losing etcd means losing cluster state.

**Q23: What is the difference between the Scheduler and Controller Manager?**
> The Scheduler assigns unscheduled pods to nodes. The Controller Manager runs control loops that reconcile actual vs desired state (e.g., ReplicaSet controller creates pods if replicas are below desired count).

**Q24: What does kubelet do?**
> Runs on every worker node. Reads PodSpecs from the API server and ensures the described containers are running and healthy. Reports pod status back to the control plane.

**Q25: What is kube-proxy?**
> A network proxy on every node. Implements Kubernetes Service networking by maintaining iptables/ipvs rules that route traffic to the correct pod(s) for a given Service.

---

### Pods & Deployments

**Q26: Why should you use a Deployment instead of creating Pods directly?**
> Direct pods are not rescheduled if they die. A Deployment manages a ReplicaSet which ensures the desired number of pods are always running, handles rolling updates, enables rollbacks, and reschedules pods if nodes fail.

**Q27: What is the difference between a ReplicaSet and a Deployment?**
> A ReplicaSet just ensures N replicas are running. A Deployment wraps a ReplicaSet and adds rolling update/rollback capabilities, maintaining history of ReplicaSets for rollbacks.

**Q28: What happens when a pod crashes in a Deployment?**
> The ReplicaSet controller detects that actual replicas < desired replicas and creates a new pod. If the pod keeps crashing, Kubernetes enters a CrashLoopBackOff state with exponential backoff between restart attempts.

**Q29: What is CrashLoopBackOff?**
> A pod status indicating a container is repeatedly crashing and Kubernetes is waiting (backing off exponentially) before restarting it. Causes: app startup error, missing config, OOMKill. Debug with `kubectl logs my-pod --previous`.

**Q30: What is a StatefulSet and how does it differ from a Deployment?**
> StatefulSets give pods stable, predictable names (pod-0, pod-1), stable network identities, and each pod gets its own PVC. They also respect ordered startup/shutdown. Used for stateful apps like databases.

---

### Services

**Q31: What are the four Service types in Kubernetes?**
> ClusterIP (internal only), NodePort (accessible on each node's IP at a static port), LoadBalancer (provisions cloud load balancer), ExternalName (maps to external DNS name).

**Q32: How does a Service know which pods to route traffic to?**
> Via label selectors. The Service targets pods whose labels match the `selector` field. kube-proxy maintains endpoints (pod IPs) matching those selectors.

**Q33: What is a headless service?**
> A Service with `clusterIP: None`. Returns individual pod IPs via DNS instead of a single virtual IP. Used with StatefulSets so each pod can be addressed individually.

**Q34: What is an Ingress and why use it instead of LoadBalancer?**
> Ingress provides HTTP/HTTPS routing at layer 7 based on host/path rules. One LoadBalancer (via Ingress Controller) can route to many services, saving cost vs one LoadBalancer per service.

**Q35: What is the difference between ClusterIP and NodePort?**
> ClusterIP is accessible only within the cluster. NodePort exposes the service on a specific port on every node's IP, making it accessible from outside the cluster.

---

### Scaling & YAML

**Q36: What is the Horizontal Pod Autoscaler?**
> A Kubernetes object that automatically scales pod replicas based on CPU, memory, or custom metrics. Requires `metrics-server` to be installed. Scales between defined min and max replica counts.

**Q37: What is a rolling update in Kubernetes?**
> Gradually replacing old pods with new ones. Controlled by `maxSurge` (how many extra pods can exist) and `maxUnavailable` (how many pods can be unavailable). Ensures zero downtime when configured correctly.

**Q38: What is the difference between `labels` and `annotations`?**
> Labels are for identifying and selecting objects (used in selectors). Annotations are for arbitrary metadata not used for selection (descriptions, tool configs, git commits). Labels affect behavior; annotations are informational.

**Q39: What are resource requests and limits?**
> Requests are the guaranteed minimum — used by the scheduler to find a suitable node. Limits are the maximum — CPU is throttled at the limit, memory triggers OOMKill if exceeded.

**Q40: What is a ConfigMap and when would you use it?**
> A ConfigMap stores non-sensitive configuration data (app settings, feature flags, config files) as key-value pairs. Decouples config from images, allowing the same image to be used across environments.

---

### Troubleshooting

**Q41: How do you debug a pod that is stuck in Pending?**

```bash
kubectl describe pod my-pod   # Look for Events section
# Common causes:
# Insufficient resources — no node has enough CPU/memory
# Node selector or affinity rules can't be satisfied
# PVC not bound
# Image pull secret missing
```

**Q42: How do you debug a pod stuck in ImagePullBackOff?**

```bash
kubectl describe pod my-pod
# Causes:
# Wrong image name or tag
# Image not pushed to registry
# Missing imagePullSecret for private registry
# Registry is down
```

**Q43: A deployment rollout is stuck. How do you investigate?**

```bash
kubectl rollout status deployment/my-app   # Shows stuck message
kubectl describe deployment my-app        # Check events and conditions
kubectl get pods -l app=my-app            # Check pod states
kubectl logs my-pod --previous            # If new pods are crashing
```

**Q44: How do you access a service that has no external IP from your local machine?**

```bash
kubectl port-forward service/my-service 8080:80
# Then access http://localhost:8080
```

**Q45: What is the difference between `kubectl apply` and `kubectl create`?**
> `kubectl create` creates a resource and fails if it already exists. `kubectl apply` creates or updates (patches) the resource based on the declared state — idempotent and preferred for GitOps/CI-CD.

---

### Docker vs Kubernetes

**Q46: Can you run Kubernetes without Docker?**
> Yes. Kubernetes uses the Container Runtime Interface (CRI) and can run with containerd, CRI-O, or other CRI-compatible runtimes. Docker is no longer the default runtime in modern Kubernetes clusters.

**Q47: What is the relationship between Docker and Kubernetes?**
> Docker builds and runs containers. Kubernetes orchestrates containers at scale. You typically build Docker images, push to a registry, and Kubernetes pulls and runs those images across a cluster.

**Q48: When would you use Docker Compose instead of Kubernetes?**
> Docker Compose for: local development, simple multi-container setups, apps that don't need high availability. Kubernetes for: production workloads at scale, auto-healing, rolling deployments, multi-node clusters.

**Q49: What is a DaemonSet and when is it used?**
> A DaemonSet ensures one pod runs on every node. Used for cluster-wide services like log collectors (Fluentd), monitoring agents (Prometheus node-exporter), and network plugins.

**Q50: What is a liveness probe vs a readiness probe?**
> Readiness: "Is the pod ready to receive traffic?" Fails → removed from Service endpoints. Liveness: "Is the pod still alive and not deadlocked?" Fails → pod is restarted.

**Q51: What is a Namespace in Kubernetes?**
> A virtual cluster providing logical isolation of resources within a physical cluster. Used to separate environments (dev/staging/prod), teams, or projects. Resources in different namespaces are isolated but can communicate.

**Q52: What are taints and tolerations?**
> Taints are applied to nodes to repel pods. Tolerations are applied to pods to allow them to be scheduled on tainted nodes. Used to dedicate nodes for specific workloads (e.g., GPU nodes, database nodes).

**Q53: What is a PodDisruptionBudget?**
> A policy that limits how many pods can be voluntarily disrupted at once (during node drains, upgrades). Ensures a minimum number of pods remain available. Example: `minAvailable: 2` ensures at least 2 pods stay up.

**Q54: What does `kubectl rollout restart` do?**
> Forces a rolling restart of all pods in a deployment without changing the image or spec. Useful for picking up new ConfigMap/Secret values or recovering from a stuck deployment.

**Q55: What is the difference between a Job and a CronJob?**
> A Job runs a task once to completion. A CronJob schedules Jobs to run repeatedly based on a cron expression (e.g., daily, hourly).

**Q56: What is `imagePullPolicy` and what are its options?**
> Controls when kubelet pulls an image. `Always` — always pull (for mutable tags like `latest`). `IfNotPresent` — pull only if not cached (for immutable versioned tags). `Never` — never pull; must be pre-loaded.

**Q57: What is the purpose of `spec.selector` in a Deployment?**
> Defines which pods this Deployment manages. The selector must match the pod template's labels. Kubernetes uses this to track and manage the right set of pods.

**Q58: What is the difference between `kubectl delete pod` and the pod being evicted?**
> `kubectl delete pod` is a voluntary termination — graceful, triggers a new pod (via ReplicaSet). Eviction is Kubernetes forcibly removing a pod from a node under resource pressure (low memory/disk). Both result in pod recreation if managed by a controller.

**Q59: What is a Service Account?**
> An identity for processes running in pods to authenticate against the Kubernetes API. Pods use service accounts to call the K8s API (e.g., for operators, controllers, or apps that need to interact with the cluster).

**Q60: How does Kubernetes handle secrets? Are they secure?**
> Secrets are stored in etcd, base64-encoded by default (not encrypted). Base64 is encoding, not encryption. Best practices: enable encryption at rest in etcd, use RBAC to restrict secret access, use external secret managers (AWS Secrets Manager, Vault) with the External Secrets Operator.

---

## 24. Quick Revision Cheat Sheet

### Docker Architecture Summary

```
CLI (docker)  ──REST API──►  Daemon (dockerd)  ──pulls from──►  Registry
                                    │
                          ┌─────────┼─────────┐
                       Images   Containers   Volumes/Networks
```

---

### Docker Command Cheat Sheet

```bash
# ── IMAGE COMMANDS ──────────────────────────────────────────────
docker build -t name:tag .           # Build image
docker pull image:tag                # Download image
docker push image:tag                # Upload image
docker images                        # List images
docker rmi image:tag                 # Remove image
docker image prune -a                # Remove all unused images
docker tag src:tag dst:tag           # Tag image
docker history image:tag             # Show layers

# ── CONTAINER COMMANDS ──────────────────────────────────────────
docker run -d -p 8080:80 --name c nginx   # Run detached with port mapping
docker run -it --rm ubuntu bash           # Interactive, remove on exit
docker ps                                 # Running containers
docker ps -a                              # All containers
docker stop/start/restart name            # Lifecycle
docker rm name                            # Remove stopped container
docker rm -f name                         # Force remove
docker logs -f name                       # Stream logs
docker exec -it name bash                 # Shell into container
docker inspect name                       # Full JSON details
docker stats                              # Live resource usage
docker cp name:/path ./local              # Copy from container

# ── VOLUME COMMANDS ─────────────────────────────────────────────
docker volume create vol                  # Create volume
docker volume ls                          # List volumes
docker volume inspect vol                 # Details
docker volume rm vol                      # Remove volume
docker volume prune                       # Remove unused volumes

# ── NETWORK COMMANDS ────────────────────────────────────────────
docker network create net                 # Create network
docker network ls                         # List networks
docker network inspect net                # Details
docker network connect net container      # Add container to network

# ── SYSTEM COMMANDS ─────────────────────────────────────────────
docker system df                          # Disk usage
docker system prune -a                    # Clean up everything
docker info                               # Docker system info

# ── COMPOSE COMMANDS ────────────────────────────────────────────
docker compose up -d                      # Start all services
docker compose up --build                 # Rebuild and start
docker compose down                       # Stop and remove
docker compose down -v                    # Also remove volumes
docker compose logs -f service            # Follow service logs
docker compose exec service bash          # Shell into service
docker compose ps                         # List service status
```

---

### Dockerfile Instruction Summary

| Instruction | Purpose | Overridable? |
| --- | --- | --- |
| `FROM` | Base image | — |
| `RUN` | Execute build command (new layer) | — |
| `CMD` | Default command/args | Yes (docker run args) |
| `ENTRYPOINT` | Fixed executable | Only with `--entrypoint` |
| `COPY` | Copy files to image | — |
| `ADD` | COPY + tar extract + URL | — |
| `WORKDIR` | Set working directory | — |
| `ENV` | Runtime environment variable | Yes (`-e` flag) |
| `ARG` | Build-time variable | Yes (`--build-arg`) |
| `EXPOSE` | Document container port | — |
| `USER` | Set process user | Yes (`--user`) |
| `LABEL` | Add metadata | — |
| `HEALTHCHECK` | Define health test | — |
| `VOLUME` | Declare mount point | — |

---

### Docker Networking & Volume Summary

```
NETWORKS                              VOLUMES
─────────────────────                 ─────────────────────
bridge (default)                      Named Volume
 └─ Containers by IP only             └─ Managed by Docker
                                       └─ Best for prod data
Custom Bridge
 └─ Containers by name (DNS)          Bind Mount
 └─ Use for all prod setups           └─ Host path ↔ container
                                       └─ Best for dev

host                                  Anonymous Volume
 └─ Share host network stack          └─ Random ID
 └─ Linux only                        └─ Created by VOLUME instruction

none
 └─ No network
```

---

### Kubernetes Architecture Summary

```
CONTROL PLANE                         WORKER NODE
─────────────────────                 ─────────────────────
API Server ← all requests             kubelet (manages pods)
Scheduler  → assigns pods             kube-proxy (iptables)
Controller Manager → reconciles       Container Runtime
etcd → stores all state               Pods (your containers)
```

---

### Kubernetes Object Hierarchy

```
Cluster
└── Namespace
    ├── Deployment
    │   └── ReplicaSet
    │       └── Pod(s)
    │           └── Container(s)
    ├── Service
    │   └── (targets Pods via labels)
    ├── Ingress
    │   └── (routes to Services)
    ├── ConfigMap / Secret
    │   └── (consumed by Pods)
    ├── PersistentVolumeClaim
    │   └── bound to PersistentVolume
    ├── Job / CronJob
    │   └── Pod(s) (runs to completion)
    ├── DaemonSet
    │   └── Pod (one per node)
    └── StatefulSet
        └── Pod(s) (with stable identity)
```

---

### kubectl Command Cheat Sheet

```bash
# ── CONTEXT / CLUSTER ───────────────────────────────────────────
kubectl config get-contexts                    # List contexts
kubectl config use-context ctx-name            # Switch context
kubectl cluster-info                           # Cluster info

# ── VIEWING RESOURCES ───────────────────────────────────────────
kubectl get pods                               # List pods (default NS)
kubectl get pods -n production                 # Specific namespace
kubectl get pods -A                            # All namespaces
kubectl get all -n production                  # All resources
kubectl get pods -l app=my-app                 # By label
kubectl get pods -o wide                       # With node info
kubectl get pods -o yaml                       # Full YAML output
kubectl describe pod my-pod                    # Detailed info + events

# ── CREATING / UPDATING ─────────────────────────────────────────
kubectl apply -f file.yaml                     # Create or update
kubectl apply -f ./k8s/                        # Apply directory
kubectl create deployment my-app --image=myapp:1.0
kubectl set image deployment/my-app app=myapp:2.0

# ── SCALING ─────────────────────────────────────────────────────
kubectl scale deployment my-app --replicas=5

# ── ROLLOUTS ────────────────────────────────────────────────────
kubectl rollout status deployment/my-app       # Watch rollout
kubectl rollout history deployment/my-app      # History
kubectl rollout undo deployment/my-app         # Roll back
kubectl rollout restart deployment/my-app      # Force restart

# ── LOGS & EXEC ─────────────────────────────────────────────────
kubectl logs my-pod                            # Logs
kubectl logs -f my-pod                         # Stream logs
kubectl logs my-pod --previous                 # Crashed container
kubectl exec -it my-pod -- bash               # Shell
kubectl exec my-pod -- ls /app                # Run command

# ── DEBUGGING ───────────────────────────────────────────────────
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl top pods                               # Resource usage
kubectl top nodes
kubectl port-forward service/my-svc 8080:80   # Local access
kubectl describe node my-node                  # Node details
kubectl run debug --image=busybox --rm -it -- sh

# ── DELETING ────────────────────────────────────────────────────
kubectl delete pod my-pod
kubectl delete deployment my-app
kubectl delete -f file.yaml
kubectl delete pods --all -n staging
```

---

### Deployment Workflow (One-Liner Summary)

```
Code → Dockerfile → docker build → docker push → kubectl apply → kubectl rollout status → Live
```

---

### Common Interview One-Liners

| Concept | One-Liner |
| --- | --- |
| **Container** | A running isolated process with its own filesystem, network, and process space |
| **Image** | Read-only layered template used to create containers |
| **Dockerfile** | Text file with instructions to build a Docker image |
| **Layer caching** | Docker reuses unchanged layers; put infrequent changes first |
| **Union FS** | Merges multiple read-only layers + one writable layer into one view |
| **Named volume** | Docker-managed persistent storage identified by a name |
| **Bind mount** | Maps a specific host path into a container |
| **Bridge network** | Default Docker network; custom bridge enables DNS by container name |
| **Docker Compose** | Tool to define and run multi-container apps via a YAML file |
| **Multi-stage build** | Multiple FROM statements; copy only artifacts to keep production image small |
| **Kubernetes** | Container orchestration platform for deploying, scaling, and managing containers |
| **Pod** | Smallest Kubernetes unit; one or more containers sharing network/storage |
| **Deployment** | Manages ReplicaSets; enables rolling updates, rollbacks, and scaling |
| **ReplicaSet** | Ensures N copies of a pod are always running |
| **Service** | Stable network endpoint (virtual IP + DNS) for a set of pods |
| **ClusterIP** | Internal-only service accessible within the cluster |
| **NodePort** | Exposes service on each node's IP at a static port |
| **LoadBalancer** | Provisions cloud load balancer for external access |
| **Ingress** | HTTP/HTTPS routing rules; routes to services based on host/path |
| **ConfigMap** | Non-sensitive key-value config storage; decouples config from images |
| **Secret** | Sensitive data storage (base64-encoded; enable encryption at rest) |
| **Namespace** | Virtual cluster for logical isolation of resources |
| **DaemonSet** | Runs one pod copy on every node (logging, monitoring agents) |
| **StatefulSet** | Pods with stable names, network identity, and per-pod storage |
| **HPA** | Automatically scales pods based on CPU/memory/custom metrics |
| **Rolling update** | Gradually replaces pods; zero-downtime with maxUnavailable=0 |
| **etcd** | Distributed key-value store; K8s single source of truth |
| **kubelet** | Node agent that ensures pod containers are running as specified |
| **Readiness probe** | "Ready for traffic?" — failure removes pod from Service endpoints |
| **Liveness probe** | "Still alive?" — failure triggers pod restart |
| **Resource requests** | Guaranteed minimum used by scheduler for placement decisions |
| **Resource limits** | Maximum allowed; CPU throttled, memory triggers OOMKill |
| **PV/PVC** | PV is provisioned storage; PVC is a user's claim for storage |
| **StorageClass** | Enables dynamic PV provisioning based on requirements |

---

*Last updated: 2025 | Covers Docker Engine 26+ and Kubernetes 1.29+*
*For production Kubernetes, always refer to the official documentation at kubernetes.io*
