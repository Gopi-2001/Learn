# Docker Compose — Basics to Advanced

---

## What is Docker Compose?

Running one container is easy. Running a **real app** — API + database + cache + proxy — means running many containers that need to talk to each other, share volumes, and start in the right order.

Docker Compose solves all of that with **one YAML file**.

```
Without Compose:                      With Compose:
docker run postgres ...               docker compose up -d
docker run redis ...                  (one command does everything)
docker run --link ... node ...
docker run --link ... nginx ...
```

---

## Mental Model

```
docker-compose.yml
        │
        ├── services   → what containers to run
        ├── volumes    → where to store persistent data
        └── networks   → how containers talk to each other
```

---

## Project Structure (What We Built)

```
my-first-compose-app/
├── docker-compose.yml           ← main file
├── docker-compose.override.yml  ← auto-merged for dev
├── docker-compose.prod.yml      ← explicit merge for prod
├── .env                         ← shared variables
├── api/
│   ├── Dockerfile
│   ├── package.json
│   └── server.js                ← Node.js API (Postgres + Redis)
├── db/
│   └── init.sql                 ← auto-runs when DB first starts
└── nginx/
    └── default.conf             ← reverse proxy config
```

**What the app does:**
- Request hits **Nginx** on port 8080
- Nginx proxies to **API** (Node.js)
- API checks **Redis** first (cache hit → return instantly)
- On cache miss → queries **Postgres**, caches result, returns data

---

## Compose File Structure

```yaml
name: myapp          # project name (prefixes all container names)

services:            # the containers
  db: ...
  redis: ...
  api: ...
  nginx: ...

volumes:             # named persistent storage
  db-data:
  redis-data:

networks:            # virtual networks
  backend:
  frontend:
```

---

## Core Concept 1 — Services

Each service = one container. Two ways to define it:

```yaml
services:
  # A. Use an existing image (no Dockerfile)
  redis:
    image: redis:7-alpine

  # B. Build from your Dockerfile
  api:
    build:
      context: ./api      # folder with the Dockerfile
      dockerfile: Dockerfile
```

---

## Core Concept 2 — Networks

By default, all services are on ONE shared network and can reach each other by **service name**.

```yaml
networks:
  backend:   # db, redis, api
  frontend:  # api, nginx
```

```
nginx  ──[frontend]──  api  ──[backend]──  db
                                  │
                              [backend]
                                  │
                               redis
```

**nginx cannot reach db directly** — it's not on the `backend` network. This is intentional security isolation.

Inside a container, you resolve other services by name:
```js
// In Node.js code — "db" and "redis" are service names
host: process.env.DB_HOST    // set to "db" in docker-compose.yml
host: process.env.REDIS_HOST // set to "redis"
```

---

## Core Concept 3 — Volumes

Two types of mounts:

### Named Volume (for databases — data survives `docker compose down`)
```yaml
volumes:
  db-data:          # declare it here

services:
  db:
    volumes:
      - db-data:/var/lib/postgresql/data  # use it here
```

### Bind Mount (for dev — live code reload)
```yaml
services:
  api:
    volumes:
      - ./api:/app   # host folder : container path
```

```
Named volume:                  Bind mount:
Docker manages the path        You control the path
Data persists always           Reflects local changes instantly
Good for databases             Good for dev code
```

---

## Core Concept 4 — Environment Variables

Three ways to inject env vars:

```yaml
# 1. Inline (for non-secrets)
environment:
  NODE_ENV: production
  PORT: 3000

# 2. From .env file (auto-loaded by Compose)
environment:
  DB_PASSWORD: ${DB_PASSWORD}   # reads from .env

# 3. env_file key (load a whole file)
env_file:
  - .env
  - .env.local
```

**.env is auto-loaded** — you never need to specify it. Variables in `.env` are available as `${VAR_NAME}` in the compose file itself.

> **Security rule:** Never store real secrets in `.env` committed to git. Use Docker Secrets or a vault in production.

---

## Core Concept 5 — depends_on & Health Checks

`depends_on` controls **start order** and optionally waits for health.

```yaml
services:
  api:
    depends_on:
      db:
        condition: service_healthy   # wait for db to pass healthcheck
      redis:
        condition: service_healthy

  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d appdb"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s    # grace period before first check
```

```
Conditions:
  service_started   → just wait for container to start (default, not reliable)
  service_healthy   → wait until healthcheck passes ✓ (use this)
  service_completed_successfully → for one-off init containers
```

---

## Core Concept 6 — Restart Policies

```yaml
restart: no                # never restart (default)
restart: always            # always restart, even after reboot
restart: on-failure        # restart only on non-zero exit
restart: unless-stopped    # restart always, except if you manually stopped it ✓
```

---

## Core Concept 7 — Override Files

Compose **automatically merges** `docker-compose.override.yml` in development.

```
docker compose up
  └── reads: docker-compose.yml  +  docker-compose.override.yml (auto)

docker compose -f docker-compose.yml -f docker-compose.prod.yml up
  └── reads: docker-compose.yml  +  docker-compose.prod.yml (explicit, no override)
```

Use this pattern:
```
docker-compose.yml          → base (always used)
docker-compose.override.yml → dev extras (auto-merged locally)
docker-compose.prod.yml     → prod extras (explicit in CI/CD)
```

---

## Essential Commands

```bash
# ── Start & Stop ──────────────────────────────────────
docker compose up              # start (foreground, see logs)
docker compose up -d           # start detached (background)
docker compose up --build -d   # rebuild images then start

docker compose down            # stop and remove containers + networks
docker compose down -v         # also remove named volumes (⚠ deletes data!)

# ── Status & Logs ─────────────────────────────────────
docker compose ps              # list all services and their state
docker compose logs            # all logs
docker compose logs api        # logs for one service
docker compose logs -f api     # follow logs (live tail)

# ── Interact ──────────────────────────────────────────
docker compose exec api sh     # shell into the api container
docker compose exec db psql -U appuser -d appdb   # postgres cli

# ── Build ─────────────────────────────────────────────
docker compose build           # build all images
docker compose build api       # build one service

# ── Scale ─────────────────────────────────────────────
docker compose up -d --scale api=3   # run 3 api containers

# ── One-off Commands ──────────────────────────────────
docker compose run api node -e "console.log('hello')"   # run a command in a new container
docker compose run --rm api sh                          # --rm removes the container after

# ── Cleanup ───────────────────────────────────────────
docker compose down --rmi all -v    # remove everything (images + volumes)
```

---

## Run the Example App

```bash
# 1. Go into the folder
cd my-first-compose-app

# 2. Start everything
docker compose up --build -d

# 3. Check all services are healthy
docker compose ps

# 4. Test the app (through nginx on port 8080)
curl http://localhost:8080/hello
# First call → source: "db"
curl http://localhost:8080/hello
# Second call (within 10s) → source: "cache"

# 5. See the visit records in Postgres
docker compose exec db psql -U appuser -d appdb -c "SELECT * FROM visits;"

# 6. Check the Redis cache
docker compose exec redis redis-cli keys "*"

# 7. Follow API logs
docker compose logs -f api

# 8. Stop everything (keeps data)
docker compose down

# 9. Stop and wipe all data
docker compose down -v
```

---

## How Services Find Each Other (DNS)

```
docker compose up creates a virtual DNS:

  Service name  →  Container IP (auto-assigned)

  "db"    → 172.18.0.2
  "redis" → 172.18.0.3
  "api"   → 172.18.0.4
  "nginx" → 172.18.0.5

Your code just uses the service name — no hardcoded IPs needed.
```

---

## Volumes Deep Dive

```bash
# List volumes
docker volume ls

# Inspect a volume (find where data lives on host)
docker volume inspect myapp_db-data

# Volume naming: <project-name>_<volume-name>
# project = myapp (from name: in compose file)
# volume  = db-data
# result  = myapp_db-data
```

```yaml
# External volume (pre-created, Compose won't manage it)
volumes:
  shared-logs:
    external: true

# Named volume with driver options (e.g. NFS)
volumes:
  nfs-data:
    driver: local
    driver_opts:
      type: nfs
      o: addr=192.168.1.1,rw
      device: ":/nfs/path"
```

---

## Advanced — Profiles (Conditional Services)

Run certain services only when needed:

```yaml
services:
  api:
    profiles: []          # always runs (no profile = default)

  db:
    profiles: []

  pgadmin:                # DB GUI — only in dev
    image: dpage/pgadmin4
    profiles: [dev]
    ports:
      - "5050:80"

  migrate:                # one-off migration runner
    build: ./api
    command: node migrate.js
    profiles: [migrate]
    depends_on:
      db:
        condition: service_healthy
```

```bash
docker compose up -d                     # starts api + db only
docker compose --profile dev up -d       # adds pgadmin
docker compose --profile migrate run migrate  # runs migration once
```

---

## Advanced — Resource Limits

```yaml
services:
  api:
    deploy:
      resources:
        limits:
          cpus: "0.5"       # max 50% of one CPU core
          memory: 256M      # max 256 MB RAM
        reservations:
          cpus: "0.25"
          memory: 128M
```

---

## Advanced — Secrets (Production-safe)

```yaml
services:
  db:
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password  # reads from file, not env
    secrets:
      - db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt   # local file (dev)
    # external: true                  # from Docker Swarm secrets (prod)
```

---

## Compose vs Dockerfile — Who Does What?

```
Dockerfile:                          docker-compose.yml:
─────────────────────────────────    ─────────────────────────────────
How to BUILD one image               How to RUN multiple services
Installs dependencies                Connects them via networks
Sets ENV defaults                    Overrides ENV at runtime
Runs as specific USER                Maps ports and volumes
Sets HEALTHCHECK                     Sets restart policy
                                     Controls start order
```

---

## Full Architecture Diagram

```
  ┌──────────────────────────────────────────────────┐
  │               Docker Compose Project              │
  │                                                  │
  │  ┌─────────[frontend network]──────────────────┐ │
  │  │                                             │ │
  │  │  ┌─────────┐    proxy     ┌───────────┐    │ │
  │  │  │  nginx  │ ──────────► │    api    │    │ │
  │  │  │  :8080  │             │   :3000   │    │ │
  │  │  └─────────┘             └─────┬─────┘    │ │
  │  │                                │           │ │
  │  └────────────────────────────────┼───────────┘ │
  │                                   │             │
  │  ┌─────────[backend network]───────┼───────────┐ │
  │  │                                │           │ │
  │  │              ┌─────────────────┤           │ │
  │  │              ▼                 ▼           │ │
  │  │        ┌──────────┐     ┌──────────┐      │ │
  │  │        │ postgres │     │  redis   │      │ │
  │  │        │  :5432   │     │  :6379   │      │ │
  │  │        └────┬─────┘     └────┬─────┘      │ │
  │  │             │                │             │ │
  │  └─────────────┼────────────────┼─────────────┘ │
  │                │                │               │
  │         ┌──────▼──────┐  ┌──────▼──────┐        │
  │         │  db-data    │  │ redis-data  │        │
  │         │ (volume)    │  │ (volume)    │        │
  │         └─────────────┘  └─────────────┘        │
  └──────────────────────────────────────────────────┘
         ↑
    localhost:8080  (only nginx is exposed to your machine)
```

---

## Quick Reference — YAML Keys

```yaml
services:
  myservice:
    image: nginx:alpine           # use existing image
    build: ./path                 # or build from Dockerfile
    container_name: my-nginx      # custom name (skip for scaling)
    restart: unless-stopped       # restart policy
    ports:
      - "host:container"          # port mapping
    environment:
      KEY: value                  # env vars
    env_file:
      - .env                      # load from file
    volumes:
      - named-vol:/path           # named volume
      - ./local:/container        # bind mount
    networks:
      - mynet                     # attach to network
    depends_on:
      other:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/health"]
      interval: 10s
      timeout: 5s
      retries: 3
      start_period: 5s
    deploy:
      replicas: 2
      resources:
        limits:
          memory: 256M
    profiles: [dev]               # only with --profile dev
    command: node server.js       # override CMD from Dockerfile
    entrypoint: ["/bin/sh", "-c"] # override ENTRYPOINT
```

---

## Common Gotchas

| Gotcha | Fix |
|---|---|
| App starts before DB is ready | Use `depends_on` with `condition: service_healthy` |
| Data lost on `docker compose down` | Use named volumes, not bind mounts for databases |
| Can't reach service by name | Make sure both services are on the same network |
| `.env` changes not picked up | Run `docker compose up -d --force-recreate` |
| Port already in use | Change the host port (`"8081:80"` instead of `"8080:80"`) |
| override file not applied | It's auto-merged only when named exactly `docker-compose.override.yml` |
