# Docker Quick Review — 5 to 7 min

---

## What is Docker?

Docker packages your app + everything it needs (OS, runtime, dependencies) into a **container** — so it runs the same everywhere.

```
Your App + Node.js + OS libs = Docker Image → runs as Container
```

---

## Image vs Container

| | Image | Container |
|---|---|---|
| What | Blueprint / recipe | Running instance of an image |
| State | Read-only | Has a thin writable layer on top |
| Analogy | Cake recipe | The actual baked cake |

---

## Image Layers

Every Dockerfile instruction creates a **read-only layer**, stacked on top of each other.

```
┌─────────────────────────┐
│  Layer 4: server.js     │  ← changes often
├─────────────────────────┤
│  Layer 3: npm install   │
├─────────────────────────┤
│  Layer 2: package.json  │
├─────────────────────────┤
│  Layer 1: node:alpine   │  ← rarely changes
└─────────────────────────┘
```

**Key rule:** Put stable things near the top, frequently changing code at the bottom → Docker reuses cached layers → **faster builds**.

---

## UnionFS & Copy-on-Write (CoW)

- **UnionFS** — merges all read-only layers into one unified filesystem that the container sees.
- **CoW** — when a container modifies a file from a read-only layer, Docker **copies it to the writable layer first**, then edits the copy. Original layer stays untouched.

```
3 containers from same nginx image:

  Container A (R/W)   Container B (R/W)   Container C (R/W)
        ↓                   ↓                   ↓
  ┌─────────────────────────────────────────────────┐
  │              nginx image layers (shared)         │  ← one copy on disk
  └─────────────────────────────────────────────────┘
```

**Benefit:** Containers start instantly, share disk space, stay isolated.

---

## Dockerfile Instructions — At a Glance

| Instruction | When it runs | Purpose |
|---|---|---|
| `FROM` | Build | Base image to start from |
| `WORKDIR` | Build | Sets working directory inside container |
| `COPY` | Build | Copies files from host → container |
| `RUN` | Build | Executes commands (installs, configs) |
| `ENV` | Build + Runtime | Sets environment variables |
| `ARG` | Build only | Build-time variables (not in running container) |
| `EXPOSE` | Documentation | Declares which port the app uses (doesn't open it) |
| `CMD` | Container start | Default command (easily overridden) |
| `ENTRYPOINT` | Container start | Fixed binary to run (CMD provides its args) |
| `USER` | Runtime | Run as non-root for security |
| `HEALTHCHECK` | Runtime | Periodically checks if app is alive |
| `LABEL` | Metadata | Adds info tags to the image |

---

## The App's Dockerfile — Annotated

```dockerfile
FROM node:20-alpine          # tiny base image with Node.js 20

LABEL maintainer="gk@example.com"
LABEL version="1.0.0"

WORKDIR /app                 # all commands run from /app

COPY package.json .          # copy manifest FIRST (cache trick)
RUN npm install              # install deps (cached unless package.json changes)
COPY server.js .             # copy app code LAST (changes often)

ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000                  # documentation only

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000', r => process.exit(r.statusCode === 200 ? 0 : 1))" || exit 1

RUN addgroup -S mygroup && adduser -S myuser -G mygroup
USER myuser                  # run as non-root (security best practice)

CMD ["node", "server.js"]    # exec form — node becomes PID 1
```

---

## CMD vs ENTRYPOINT

```
CMD   → default command, easily overridden at runtime
ENTRYPOINT → always runs this binary; CMD just provides its default args

docker run my-app                 # node server.js
docker run my-app other.js        # node other.js   (ENTRYPOINT stays, CMD overridden)
docker run --entrypoint python my-app  # only way to override ENTRYPOINT
```

---

## Port Mapping

```
EXPOSE 3000  →  documents the port (inside container)

docker run -p 8080:3000 my-app
              ↑      ↑
         your PC   container

Visit: localhost:8080 → reaches container port 3000
```

---

## Key Commands

```bash
# Build image
docker build -t my-first-app .

# Run container (detached, with port mapping)
docker run -d -p 8080:3000 --name my-container my-first-app

# Test it
curl http://localhost:8080

# Shell into running container
docker exec -it my-container sh

# Check health & status
docker ps

# View logs
docker logs my-container

# Stop & remove
docker stop my-container
docker rm my-container

# List images
docker images
```

---

## Security Checklist

- Use a pinned base image (`node:20`, not `node:latest`)
- Copy `package.json` before source code (layer cache optimization)
- Run as a non-root `USER`
- Never put secrets in `ENV` (visible via `docker inspect`)
- Use `HEALTHCHECK` so orchestrators know when your app is unhealthy

---

## Mental Model Summary

```
Dockerfile  →  docker build  →  Image  →  docker run  →  Container
(recipe)                      (cake)                    (served slice)
```
