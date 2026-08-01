Reference for Docker Compose commands, tailored to your setup where you're using a non-default filename (`services.docker-compose.yaml`).

## The `-f` flag — when you need it

By default, Docker Compose looks for a file named `docker-compose.yml`, `docker-compose.yaml`, `compose.yml`, or `compose.yaml` in your current directory. **If your file has any other name** (like yours, `services.docker-compose.yaml`), you must tell Compose which file to use every single time with `-f <filename>`.

```bash
docker compose -f services.docker-compose.yaml up -d
```

If you rename your file to just `docker-compose.yaml`, you can drop `-f` entirely and all commands get shorter. That's the standard convention — worth doing if this annoys you.

## Core commands

### Start everything
```bash
docker compose -f services.docker-compose.yaml up -d
```
- `up` — creates and starts all containers defined in the file
- `-d` (detached) — runs in the background, gives you your terminal back. **Without `-d`, your terminal gets flooded with live logs and locks up until you Ctrl+C** (which also stops the containers). Almost always use `-d` for normal usage.

### Stop everything (keep data)
```bash
docker compose -f services.docker-compose.yaml stop
```
Stops containers but doesn't remove them. Volumes, networks, and containers stay intact. Use this for "I'm done for the day, keep my data."

### Start again after `stop`
```bash
docker compose -f services.docker-compose.yaml start
```

### Restart (stop + start in one go)
```bash
docker compose -f services.docker-compose.yaml restart
```
Useful after editing environment variables that don't need a full rebuild, or just to force a service to reload.

### Stop and remove containers (keep volumes/data)
```bash
docker compose -f services.docker-compose.yaml down
```
Removes containers and the network, but **volumes survive** (your postgres/kafka/redis data is safe). This is your go-to for "tear down and rebuild cleanly" without losing data.

### Stop, remove containers, AND wipe all data
```bash
docker compose -f services.docker-compose.yaml down -v
```
`-v` also deletes volumes. **This is destructive** — you lose all database data, Kafka topics, Redis cache, everything. Use this when you want a truly fresh start (which is what fixed your postgres issue earlier, since stale volume data was the problem).

## Checking status and logs

### See what's running
```bash
docker compose -f services.docker-compose.yaml ps
```
Shows container status: `Up`, `Restarting`, `Exited`, and whether healthchecks pass.

### View logs — all services
```bash
docker compose -f services.docker-compose.yaml logs
```

### View logs — one specific service
```bash
docker compose -f services.docker-compose.yaml logs kafka
```
Use the **service name** from your YAML (e.g. `kafka`, `postgres`, `control-center`), not the container_name.

### Follow logs live (like `tail -f`)
```bash
docker compose -f services.docker-compose.yaml logs -f control-center
```
`-f` here means "follow" (different meaning from the top-level `-f` for filename — context matters). Press Ctrl+C to stop following (doesn't stop the container).

### See only the last N lines
```bash
docker compose -f services.docker-compose.yaml logs --tail=50 control-center
```
Useful when a service has been running a while and you don't want to scroll through hours of logs.

## Working with individual services

### Start/stop just one service
```bash
docker compose -f services.docker-compose.yaml up -d kafka
docker compose -f services.docker-compose.yaml stop redis
```

### Restart just one service
```bash
docker compose -f services.docker-compose.yaml restart kafka
```
Handy when only one service is misbehaving — no need to touch the others.

### Rebuild and recreate one service (after editing the YAML)
```bash
docker compose -f services.docker-compose.yaml up -d --force-recreate kafka
```
`--force-recreate` throws away the existing container and makes a new one, even if the config looks unchanged. Useful when Compose thinks nothing changed but you know it did.

## Running a command inside a running container

```bash
docker compose -f services.docker-compose.yaml exec control-center curl -sv http://localhost:9021
```
`exec` runs a command inside an **already-running** container (what we used to test connectivity earlier). Requires the container to be up.

## Getting a shell inside a container

```bash
docker compose -f services.docker-compose.yaml exec postgres sh
```
Drops you into an interactive shell inside that container. Use `bash` instead of `sh` if the image has bash (alpine-based images like yours usually only have `sh`).

## Validate your YAML without running it

```bash
docker compose -f services.docker-compose.yaml config
```
Parses and prints the fully-resolved config. Great for catching syntax errors or seeing what environment variables actually resolved to, before you `up`.

## Quick cheat sheet

| Command | Effect |
|---|---|
| `up -d` | start everything, background |
| `down` | stop + remove containers, **keep data** |
| `down -v` | stop + remove containers, **wipe data** |
| `stop` / `start` | pause/resume without deleting anything |
| `ps` | see status of all services |
| `logs -f <service>` | live-tail one service's logs |
| `restart <service>` | restart just one service |
| `exec <service> <cmd>` | run a command inside a running container |
| `config` | validate/preview the resolved YAML |

---

One tip: rename `services.docker-compose.yaml` → `docker-compose.yaml` in your project folder, and every command above gets shorter since Compose finds it automatically — no more typing `-f` every time.