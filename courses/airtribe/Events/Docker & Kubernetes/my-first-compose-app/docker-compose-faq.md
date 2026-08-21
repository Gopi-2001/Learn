# Docker Compose — Questions, Answers & FAQ

---

## Q: `redis-data:/data` — which is host, which is container, and what does "host" mean here?

```
redis-data:/data
    ↑          ↑
  "host"    container path
```

**`redis-data`** — this is a **named volume**, not a folder on your machine. Docker manages it internally. Think of it as a labelled storage box that Docker keeps somewhere on your PC (usually under `C:\ProgramData\docker\volumes\myapp_redis-data\`). You don't pick the path — Docker does.

**`/data`** — the path **inside the container** where Redis writes its snapshot files.

### "Host" means two different things depending on the mount type

| Mount type | Left side (host) | Example |
|---|---|---|
| **Named volume** | A Docker-managed label | `redis-data:/data` |
| **Bind mount** | An actual folder on your machine | `./api:/app` |

So in `redis-data:/data`, the "host" side is not a real folder you can browse — it's a Docker abstraction. Redis writes to `/data` inside the container, and Docker silently persists that to the named volume on your disk.

Compare with the bind mount in `docker-compose.override.yml`:
```yaml
- ./api:/app
#   ↑      ↑
# real     container
# folder   path
# on your PC
```

**Why use a named volume for Redis instead of a bind mount?**  
You don't need to read or edit Redis's data files directly — you just want them to survive a restart. Named volumes are faster, portable, and Docker cleans them up properly with `docker compose down -v`.

---

## Q: If named volumes can be wiped with `down -v`, isn't that bad for persistent data like Postgres?

Yes, exactly — that's the key distinction.

`docker compose down -v` is a **deliberate, destructive command** that you only run when you actually want to wipe all data (e.g. resetting a dev environment). It is never run in production.

```
docker compose down        → stops containers, removes networks — volumes SAFE ✓
docker compose down -v     → same + deletes all named volumes — DATA GONE ✗
```

In practice:
```
Dev machine:   down -v is fine — "give me a clean slate"
Production:    you would NEVER run down -v — your database lives there
```

For extra safety in production, mark a volume as `external` — Compose will refuse to create or delete it automatically:

```yaml
volumes:
  db-data:
    external: true   # Compose errors out if this volume doesn't already exist
                     # and down -v will NOT delete it
```

With `external: true` you create the volume manually once:
```bash
docker volume create myapp_db-data
```

Then Compose just uses it — and can never accidentally destroy it. This is the standard pattern for production databases.

---

## Q: What is `default.conf` (Nginx config)?

`default.conf` is Nginx's site configuration file. When the Nginx container starts, it reads this file to know **what to do with incoming requests**.

```nginx
server {
  listen 80;          # Nginx listens on port 80 inside the container
                      # (docker-compose maps your port 8080 → this 80)

  location / {        # "for ANY request path..."

    proxy_pass http://api:3000;   # ...forward it to the api service on port 3000
                                  # "api" is the service name — Compose DNS resolves it

    proxy_set_header Host $host;             # pass the original Host header through
    proxy_set_header X-Real-IP $remote_addr; # pass the real client IP through
  }
}
```

**The full request journey:**
```
Your browser
  → localhost:8080          (your machine)
  → nginx container :80     (port mapping 8080→80)
  → proxy_pass api:3000     (Nginx forwards to api service)
  → Node.js handles it
```

Without this file, Nginx would just show its default welcome page. This file turns Nginx from a static web server into a **reverse proxy** — a traffic forwarder that sits in front of your app.

**Why is it called `default.conf`?**  
Nginx loads all `*.conf` files from `/etc/nginx/conf.d/`. The name `default` is just convention for the primary site. You could name it `myapp.conf` and it would work the same way.

---

## Q: `server.js` reads DB/Redis from env vars — why do we need separate containers? What is the point of `.env` and `init.sql`?

You've hit on a real architectural decision teams make.

**You're right — you don't HAVE to run db/redis in Compose.** Your Node.js code doesn't care where Postgres lives — it only reads from env vars:

```js
const db = new Pool({
  host: process.env.DB_HOST,   // could be "db" (local container)
  port: process.env.DB_PORT,   // could be "prod-db.company.com" (remote server)
});
```

Change `.env` and the same code connects to a completely different database — no code change needed.

### So what does each file actually do?

**`.env`** — is just a convenience file for **your local machine**. In production, a DevOps team injects these values via Kubernetes secrets, AWS Parameter Store, CI/CD pipeline variables etc. The `.env` file never goes to prod.

```
Local dev:   values come from .env file
Production:  values come from Kubernetes/AWS/Azure secrets
Same code. Different .env.
```

**`init.sql`** — only runs when the **local dev Postgres container** is created for the first time. If the DB is managed by another team on a real server, they handle schema creation themselves — this file is irrelevant to them.

**`db` and `redis` services in Compose** — purely for **local development convenience** so you don't need Postgres or Redis installed on your machine.

### The real-world split

```
Dev machine (you)              Production (DevOps/DBA team)
──────────────────             ──────────────────────────────
docker compose up              AWS RDS (Postgres)
  → spins up local db          AWS ElastiCache (Redis)
  → spins up local redis       Kubernetes (runs your api container)
  → spins up api
  → all via .env               Secrets injected by CI/CD pipeline
```

Your `api` container is the only thing that actually ships to production. The `db` and `redis` services in Compose are just "fake prod" for your laptop.

### Why bother with local containers at all then?

- No need to install Postgres or Redis on your machine
- Every developer gets an identical environment — no "works on my machine"
- You can wipe and reset with `docker compose down -v` without touching anyone else's data
- No shared dev database that someone's bad migration breaks for the whole team

---

## Q: So I don't need Compose in production — where is it actually used in real-world systems?

Honest answer — **Compose is rarely used in production at scale**. Here's where things actually land:

```
Small / side project          → Docker Compose on a single VPS ✓
Medium (1-20 services)        → Compose OR move to Kubernetes
Large / enterprise            → Kubernetes (K8s) always
```

### Where Compose IS used in production

**1. Single VPS deployments** (small SaaS, internal tools)
```
One $20/month DigitalOcean droplet
docker compose up -d
Done. No K8s complexity needed.
```

**2. CI/CD pipelines** — spinning up dependencies for tests
```yaml
# GitHub Actions
services:
  postgres:
    image: postgres:16
  redis:
    image: redis:7
# Run your tests against real db/redis, throw away after
```

**3. Self-hosted tools** (Grafana, GitLab, Keycloak)  
— These ship with a `docker-compose.yml` as their install method.

### What replaces Compose at scale — Kubernetes

Kubernetes does everything Compose does, plus auto-healing, rolling deployments, and horizontal scaling:

```
Compose                        Kubernetes equivalent
──────────────────────         ──────────────────────────────
services                   →   Pods / Deployments
networks                   →   Services + Ingress
volumes                    →   PersistentVolumeClaims
depends_on                 →   Init containers + readiness probes
docker compose scale api=3 →   replicas: 3  (auto-healed if a pod dies)
.env                       →   ConfigMaps + Secrets
docker-compose.prod.yml    →   Helm charts / Kustomize overlays
```

### Why learn Compose then?

```
Compose teaches you the concepts:
  - services, networks, volumes, env vars, health checks

Kubernetes uses the exact same concepts — just more powerful.

Compose → Kubernetes is a natural progression, not a throwaway.
```

---

## FAQ — Other Common Questions

---

### What is the difference between `docker compose` and `docker-compose`?

```
docker-compose   → old standalone CLI tool (v1, written in Python)
docker compose   → new built-in Docker plugin (v2, written in Go) ✓

Always use: docker compose  (no hyphen)
```

---

### What happens if I don't use `depends_on`?

All services start in parallel. Your API may try to connect to Postgres before Postgres is ready → connection refused crash. `depends_on` with `service_healthy` makes the API wait until Postgres passes its healthcheck before starting.

---

### What is `restart: unless-stopped` vs `restart: always`?

```
always           → restarts on crash AND on Docker daemon restart (e.g. machine reboot)
unless-stopped   → same, BUT if you manually ran `docker compose stop`, it stays stopped ✓
on-failure       → only restarts on non-zero exit code (crash), not on manual stop
no               → never restarts (default)
```

`unless-stopped` is the most practical for production-like local setups.

---

### Why does Nginx sit in front of the API instead of exposing the API port directly?

```
Direct (no nginx):                With nginx:
localhost:3000 → api              localhost:8080 → nginx → api

Nginx gives you:
  - SSL termination (HTTPS in one place)
  - Load balancing across multiple api replicas
  - Rate limiting, request buffering
  - Static file serving
  - Single public entry point
```

---

### What does `:ro` mean on a volume mount?

```yaml
- ./nginx/default.conf:/etc/nginx/conf.d/default.conf:ro
```

`ro` = read-only. The container can read the file but cannot modify it. Prevents a compromised container from altering your config files on the host.

---

### Can two services on different networks talk to each other?

No — that's the point. A service must be on the same network to reach another service. In our setup:

```
nginx  → only on frontend → cannot reach db or redis
api    → on both networks → can reach db, redis (backend) and nginx (frontend)
db     → only on backend  → cannot be reached by nginx
```

---

### What is the difference between `CMD` in Dockerfile and `command` in Compose?

```yaml
# Dockerfile
CMD ["node", "server.js"]        # default — used if nothing overrides it

# docker-compose.yml
command: node other.js           # overrides the Dockerfile CMD for this service
```

`command` in Compose always wins over `CMD` in Dockerfile.

---

### How do I rebuild just one service without restarting everything?

```bash
docker compose up -d --build api   # rebuild and restart only the api service
```

---

### How do I see which container is using which port?

```bash
docker compose ps        # shows ports, status, health for all services
docker ps                # same but for all containers on your machine
```
