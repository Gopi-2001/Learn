# Docker Learning Notes

---

## Q: what is docker image layer, explain me in easy terms

Think of a Docker image like a **stack of transparent slides** (like overhead projector sheets).

## Each slide = one layer

Every instruction in a `Dockerfile` creates a new layer on top of the previous one:

```dockerfile
FROM ubuntu          # Layer 1 - base OS
RUN apt-get update   # Layer 2 - updated packages
RUN apt-get install java  # Layer 3 - Java installed
COPY app.jar /app/   # Layer 4 - your app code
```

```
┌─────────────────────┐
│  Layer 4: app.jar   │  ← your change
├─────────────────────┤
│  Layer 3: Java      │
├─────────────────────┤
│  Layer 2: updates   │
├─────────────────────┤
│  Layer 1: Ubuntu    │  ← base
└─────────────────────┘
```

## Why this matters

**Layers are cached and shared:**
- If Layer 1, 2, 3 didn't change — Docker reuses them from cache
- Only Layer 4 gets rebuilt → **faster builds**
- Two images sharing the same base layer? They **share that layer on disk** → less storage

**Layers are read-only:**
- All layers in an image are locked/immutable
- When a container *runs*, Docker adds one thin **writable layer on top**
- When the container is deleted, that writable layer is gone — the image layers remain untouched

## Practical tip

Put things that **change often** (your app code) at the **bottom of the Dockerfile** — so Docker can reuse the cached layers above it (dependencies, OS tools) without rebuilding them every time.

```dockerfile
FROM ubuntu          # rarely changes → cache stays valid
RUN apt-get install java  # rarely changes → cache stays valid
COPY app.jar /app/   # changes often → only this rebuilds ✓
```

---

## Q: what is unionFS and CoW in Docker ? Explain in easy to understand and right example

## UnionFS (Union File System)

Think of it like **transparent folders stacked on top of each other** that appear as ONE single folder.

### Real-world analogy:
Imagine you have 3 binders:
- **Binder 1** (bottom): Company rules (read-only)
- **Binder 2** (middle): Department rules (read-only)
- **Binder 3** (top): Your personal notes (writable)

When you look through all 3, you see **one combined view**. That's UnionFS.

```
You see ONE unified filesystem:
/bin, /etc, /app, /usr ...

But it's actually made of stacked layers:
┌──────────────────────────┐
│  Container layer (R/W)   │  ← writable, your changes go here
├──────────────────────────┤
│  Layer 3: app.jar        │  ← read-only
├──────────────────────────┤
│  Layer 2: Java           │  ← read-only
├──────────────────────────┤
│  Layer 1: Ubuntu         │  ← read-only
└──────────────────────────┘
```

UnionFS **merges** all these layers so the container sees them as one filesystem.

---

## CoW (Copy-on-Write)

The rule is simple: **"Don't copy unless you absolutely need to modify."**

### Real-world analogy:
You and your colleague share a **Google Doc** (read-only for you).  
You want to edit it → Google makes **your own private copy** first, then you edit that copy.  
The original doc stays untouched.

### In Docker:

```
Image layers (read-only):
┌──────────────────────────┐
│  Layer: Ubuntu + Java    │  contains  /etc/config.txt
└──────────────────────────┘

Container runs → wants to EDIT /etc/config.txt:

Step 1: Docker COPIES /etc/config.txt  ──→  Container writable layer
Step 2: Your edit happens on the COPY
Step 3: Original layer stays untouched

┌──────────────────────────┐
│  Container layer (R/W)   │  /etc/config.txt  ← modified copy lives here
├──────────────────────────┤
│  Image Layer             │  /etc/config.txt  ← original, untouched
└──────────────────────────┘
```

---

## How they work together — Concrete Example

```
You run 3 containers from the SAME nginx image:

         Container A    Container B    Container C
         (writable)     (writable)     (writable)
              ↓               ↓              ↓
         ┌─────────────────────────────────────┐
         │         nginx image layers          │  ← SHARED (UnionFS)
         │     (read-only, stored once)        │
         └─────────────────────────────────────┘
```

- **UnionFS** lets all 3 containers share the same image layers → saves disk space
- **CoW** ensures when Container A modifies a file, it gets its own copy → other containers are **not affected**

---

## Summary

| Concept | What it does | Benefit |
|---|---|---|
| **UnionFS** | Merges multiple layers into one view | Shared layers = less disk space |
| **CoW** | Copies a file only when you modify it | Fast container start, isolated changes |

Together: containers start **instantly** (no need to copy the whole image), use **minimal disk space**, and remain **isolated** from each other.

---

## Q: I have difficulty in understanding Dockerfile. I am a beginner know very little. Can you help me understand below concept with proper hand-ons, I will lot of handholding in this.

# Dockerfile — Beginner's Complete Hands-On Guide

Let me take you step by step. We'll build **one simple Node.js app** and use it throughout every explanation.

---

## First — What IS a Dockerfile?

Think of a Dockerfile as a **recipe card** for cooking.

```
Recipe: Chocolate Cake
─────────────────────
1. Start with a plain cake base      ← FROM
2. Add flour and sugar               ← RUN
3. Set oven temperature to 180°C     ← ENV
4. Bake for 30 minutes               ← CMD
```

Docker reads this recipe top-to-bottom and **bakes** your image. That image is like the finished cake — ready to serve (run as a container).

---

## Step 0 — Setup: Create Your App First

Before writing a Dockerfile, you need an app. Create this folder structure on your machine:

```
my-first-docker-app/
├── Dockerfile
├── package.json
└── server.js
```

**server.js** — a tiny web server:
```js
const http = require('http');

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Hello from Docker! 🐳\n');
});

server.listen(3000, () => {
  console.log('Server running on port 3000');
});
```

**package.json:**
```json
{
  "name": "my-first-docker-app",
  "version": "1.0.0",
  "main": "server.js"
}
```

---

## Now let's understand each instruction one by one

---

### 1. `FROM` — "What kitchen are we starting in?"

```dockerfile
FROM node:20-alpine
```

**Simple analogy:** You walk into a kitchen. `FROM` picks which kitchen it is.
- `node:20-alpine` = a kitchen that already has Node.js 20 installed, on a tiny Alpine Linux OS.
- You don't install Node.js yourself — it's already there!

```
Without FROM node:20-alpine:         With FROM node:20-alpine:
┌──────────────────────────┐         ┌──────────────────────────┐
│  Empty kitchen           │         │  Kitchen with Node.js    │
│  No tools, no nothing    │         │  pre-installed ✓         │
└──────────────────────────┘         └──────────────────────────┘
```

**Why `alpine`?** Alpine is a tiny Linux — only ~5MB vs Ubuntu's ~80MB. Smaller = faster to download and run.

**Why pin `:20` not `:latest`?**
```dockerfile
FROM node:latest   ❌  # Today it's v20, tomorrow it could be v22 — your app might break!
FROM node:20       ✓   # Always exactly v20
```

---

### 2. `WORKDIR` — "Go to your work desk"

```dockerfile
WORKDIR /app
```

**Simple analogy:** You walk into the kitchen and sit at a specific workbench. All your work happens here.

Without `WORKDIR`, your files land in random places. With it, everything goes neatly into `/app` inside the container.

```
Container filesystem BEFORE WORKDIR:    AFTER WORKDIR /app:
/                                        /
├── bin/                                 ├── bin/
├── usr/                                 ├── usr/
└── ...scattered files...               └── app/        ← your tidy desk
                                             └── (your files go here)
```

**Hands-on check:** If you later do `COPY server.js .` — the `.` means `/app` because of `WORKDIR`. Without it, `.` means `/` — messy!

---

### 3. `COPY` — "Bring your ingredients to the desk"

```dockerfile
COPY package.json .
COPY server.js .
```

**Simple analogy:** You grab your ingredients from your bag and place them on your workbench.

`COPY <from-your-computer> <to-inside-container>`

```
Your computer:              Container (/app):
├── server.js    ──COPY──►  ├── server.js
├── package.json ──COPY──►  └── package.json
└── Dockerfile              (Dockerfile is NOT copied — it's just the recipe)
```

**Important trick — why copy `package.json` BEFORE source code:**
```dockerfile
# GOOD ✓ — smarter layer caching
COPY package.json .          # Layer A: only changes if package.json changes
RUN npm install              # Layer B: only reruns if Layer A changed
COPY server.js .             # Layer C: your code (changes often)

# BAD ✗ — wasteful
COPY . .                     # Copies everything at once
RUN npm install              # Reruns npm install even if only server.js changed!
```

---

### 4. `RUN` — "Do something while cooking"

```dockerfile
RUN npm install
```

**Simple analogy:** You're actually doing work in the kitchen — mixing ingredients, chopping vegetables. Each `RUN` is an action during the **build** process.

```
docker build  ←── this is when RUN executes
docker run    ←── container is already built, RUN doesn't run here
```

**Combine commands to save layers:**
```dockerfile
# BAD ✗ — creates 3 separate layers
RUN apt-get update
RUN apt-get install -y curl
RUN rm -rf /var/lib/apt/lists/*

# GOOD ✓ — one single layer
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*
```

Think of it like: doing 3 dishes means 3 washing-up sessions. Doing it all at once = 1 washing-up session.

---

### 5. `ENV` — "Set the kitchen temperature"

```dockerfile
ENV PORT=3000
ENV NODE_ENV=production
```

**Simple analogy:** Before you start baking, you set the oven to a temperature. These settings are available **while building AND when the container runs**.

```
Inside the container, you can read these:
process.env.PORT        → "3000"
process.env.NODE_ENV    → "production"
```

**Override at runtime:**
```bash
docker run -e PORT=8080 my-app    # Overrides PORT to 8080 just for this run
```

**Never store secrets in ENV!**
```dockerfile
ENV DB_PASSWORD=secret123   ❌  # Anyone can see this with `docker inspect`
```
Use Docker Secrets or `.env` files outside the image for sensitive data.

---

### 6. `ARG` — "Pre-build ingredient selection"

```dockerfile
ARG APP_VERSION=1.0.0
RUN echo "Building version ${APP_VERSION}"
```

**Simple analogy:** Before the kitchen even opens, your manager tells you "today we're making chocolate, not vanilla". `ARG` is that pre-decision.

**Key difference from ENV:**
```
                  ARG             ENV
During build?     ✓ YES           ✓ YES
In running
container?        ✗ NO            ✓ YES
```

```bash
# Pass it at build time
docker build --build-arg APP_VERSION=2.0.0 .
```

---

### 7. `EXPOSE` — "Put a sign on the door"

```dockerfile
EXPOSE 3000
```

**Simple analogy:** You put a sign on your restaurant door saying "We're open — enter through this door." It's documentation, not a lock or key.

`EXPOSE` **does NOT** actually open the port. It just tells people which port the app uses.

To actually open it:
```bash
docker run -p 8080:3000 my-app
#              ↑     ↑
#         your PC  container
# Visit localhost:8080 → reaches container's port 3000
```

---

### 8. `CMD` — "What does the waiter serve by default?"

```dockerfile
CMD ["node", "server.js"]
```

**Simple analogy:** The default menu item. When a customer says "just give me the usual", this is what they get.

`CMD` runs when the **container starts** (not during build like `RUN`).

```bash
docker run my-app                    # Runs: node server.js  (the default)
docker run my-app node other.js      # Runs: node other.js   (overridden!)
```

**Always use exec form (array syntax):**
```dockerfile
CMD ["node", "server.js"]    ✓  # Exec form — node is PID 1
CMD node server.js           ✗  # Shell form — shell is PID 1, node is a child
```

Why does it matter? PID 1 receives signals (like Ctrl+C to stop). With shell form, your app might not stop cleanly.

---

### 9. `ENTRYPOINT` — "The restaurant only serves pizza"

```dockerfile
ENTRYPOINT ["node"]
CMD ["server.js"]
```

**Simple analogy:** `ENTRYPOINT` is the restaurant's specialty — they ONLY serve pizza. You can choose toppings (`CMD`), but it's always pizza (`ENTRYPOINT`).

```bash
docker run my-app                  # Runs: node server.js
docker run my-app other.js         # Runs: node other.js  (changed the file, not node!)
docker run --entrypoint python my-app  # Only way to override ENTRYPOINT
```

**CMD vs ENTRYPOINT quick comparison:**
```
                   CMD                     ENTRYPOINT
Override?          Easy (just add command)  Need --entrypoint flag
Use for?           Default command/args     Fixed binary (always runs)
Together?          Provides default args    Sets the main program
```

---

### 10. `USER` — "Let a non-admin do the serving"

```dockerfile
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
```

**Simple analogy:** You have a restaurant owner (root) and a waiter (appuser). Customers are served by the waiter — not the owner who has keys to everything.

By default, Docker runs as **root** — that's dangerous! If your app gets hacked, the attacker has full root access.

```
Root user:   Can do EVERYTHING in the container (dangerous)
appuser:     Can only do what they need to (safe ✓)
```

---

### 11. `HEALTHCHECK` — "Is the kitchen still working?"

```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1
```

**Simple analogy:** Every 30 seconds, a manager knocks on the kitchen door and asks "Are you okay in there?" If no answer 3 times → mark as unhealthy.

```bash
docker ps
# CONTAINER ID   STATUS
# abc123         Up 2 minutes (healthy)    ✓
# def456         Up 5 minutes (unhealthy)  ✗ — something's wrong!
```

---

### 12. `LABEL` — "Stickers on the container"

```dockerfile
LABEL maintainer="you@example.com"
LABEL version="1.0.0"
```

**Simple analogy:** Sticky notes on a file folder. Doesn't change what's inside, just gives information.

```bash
docker inspect my-app | grep -A5 Labels
```

---

## Putting It All Together — Your First Dockerfile

Now create this `Dockerfile` in `my-first-docker-app/`:

```dockerfile
# Step 1: Start with Node.js 20 on tiny Alpine Linux
FROM node:20-alpine

# Step 2: Add some info labels
LABEL maintainer="yourname@example.com"
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
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:3000/health || exit 1

# Step 10: Run as non-root user (security!)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Step 11: Start the app when container runs
CMD ["node", "src/server.js"]
```

---

## Hands-on: Build and Run It

```bash
# 1. Go into your folder
cd my-first-docker-app

# 2. Build the image (-t gives it a name)
docker build -t my-first-app .

# 3. See your image
docker images

# 4. Run it! (-p maps port, -d runs in background)
docker run -d -p 8080:3000 --name my-container my-first-app

# 5. Test it!
curl http://localhost:8080
# Output: Hello from Docker! 🐳

# 6. Check health status
docker ps

# 7. See logs
docker logs my-container

# 8. Stop and clean up
docker stop my-container
docker rm my-container
```

---

## Visual Summary — What Runs When

```
docker build .               docker run my-app
─────────────────            ──────────────────
FROM      ✓ runs             FROM      (already done)
RUN       ✓ runs             RUN       (already done)
COPY      ✓ runs             COPY      (already done)
ENV       ✓ sets             ENV       ✓ available
WORKDIR   ✓ sets             WORKDIR   (already done)
EXPOSE    ✓ documents        CMD       ✓ executes NOW
LABEL     ✓ adds             ENTRYPOINT ✓ executes NOW
ARG       ✓ available        HEALTHCHECK ✓ runs NOW
CMD       (saved for later)
ENTRYPOINT (saved for later)
```

---

## Quick Reference Card

| Instruction | When it runs | One-line purpose |
|---|---|---|
| `FROM` | Build | Pick the base image |
| `WORKDIR` | Build | Set the working folder |
| `COPY` | Build | Copy files into image |
| `ADD` | Build | Like COPY + handles URLs/tars |
| `RUN` | Build | Execute a shell command |
| `ARG` | Build only | Build-time variable |
| `ENV` | Build + Run | Environment variable |
| `EXPOSE` | Build (docs) | Document which port |
| `LABEL` | Build (docs) | Add metadata |
| `USER` | Build + Run | Switch to this user |
| `HEALTHCHECK` | Run | Test if container is healthy |
| `CMD` | Run | Default command (overridable) |
| `ENTRYPOINT` | Run | Fixed executable (strict) |
| `VOLUME` | Run | Create a mount point |

Start with just `FROM`, `WORKDIR`, `COPY`, `RUN`, and `CMD` — those 5 are enough for 90% of real Dockerfiles!

---

## Q: here we have shell form and then Exec form and chain commands, So if preferred is exec then chaining should also be done with exec form

Great catch! This is actually a **contradiction in those notes**, and you're right to question it. Here's the truth:

## The Real Answer

**Exec form CANNOT chain commands** — because exec form bypasses the shell entirely. Shell operators like `&&`, `\`, `|`, `>` simply don't work in exec form.

```dockerfile
# This BREAKS — && is a shell operator, exec form has no shell
RUN ["apt-get", "update", "&&", "apt-get", "install", "-y", "curl"]  ❌
```

To chain with exec form, you'd have to explicitly call the shell yourself:
```dockerfile
RUN ["/bin/sh", "-c", "apt-get update && apt-get install -y curl"]  ← defeats the purpose
```

That's just shell form in disguise — pointless.

---

## The Correct Rule (that note got it wrong)

| Instruction | Preferred Form | Why |
|---|---|---|
| `RUN` | **Shell form** with `&&` chaining | Chaining is needed; shell is expected here |
| `CMD` | **Exec form** | Avoids shell as PID 1; signals work correctly |
| `ENTRYPOINT` | **Exec form** | Same reason as CMD |

```dockerfile
# RUN → shell form is correct ✓
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

# CMD → exec form is correct ✓
CMD ["node", "server.js"]

# ENTRYPOINT → exec form is correct ✓
ENTRYPOINT ["nginx", "-g"]
```

---

## Why exec form matters for CMD/ENTRYPOINT (not RUN)

```
Shell form CMD:         Exec form CMD:
PID 1 = /bin/sh         PID 1 = node  ← your app directly
PID 2 = node

Ctrl+C → signal goes    Ctrl+C → signal goes
to /bin/sh              directly to node ✓
(may not reach node!)   (app shuts down cleanly)
```

**Bottom line:** The note should never have said "exec form preferred" under `RUN`. That advice applies to `CMD` and `ENTRYPOINT` only.

---

## Q: 1. rm -rf /var/lib/apt/lists/* -> why we remove this ? 2. -y curl -> what does this means ? 3. --no-install-recommends -> what is the use of this?

Great questions! All three are about keeping your Docker image **small and clean**.

---

## 1. `rm -rf /var/lib/apt/lists/*` — Why remove this?

When you run `apt-get update`, it downloads a **list of available packages** from the internet and stores them in `/var/lib/apt/lists/`.

```
apt-get update
    ↓
Downloads package index files → saves to /var/lib/apt/lists/
(These are like a catalog: "here are all packages available to install")
```

After installation, **you don't need that catalog anymore**. But it stays on disk — wasting space inside the image.

```dockerfile
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*   ← delete the catalog after use
```

```
Without cleanup:    With cleanup:
Image = 180MB       Image = 140MB  ✓ smaller!
```

**Why in the SAME RUN step?**

Because each `RUN` creates a layer. If you delete in a separate step:
```dockerfile
RUN apt-get update && apt-get install -y curl   # Layer 1 — files exist here
RUN rm -rf /var/lib/apt/lists/*                 # Layer 2 — deleted HERE
```
The files still exist in Layer 1 — Docker keeps all layers! The delete only hides them, doesn't actually free space.

Same `RUN` = same layer = actually gone. ✓

---

## 2. `-y` in `apt-get install -y curl` — What does it mean?

`-y` means **"yes to all prompts"**.

Without `-y`, apt-get stops and asks:
```
Do you want to continue? [Y/n]  ← waiting for human input
```

Inside a Docker build, **nobody is there to type Y**. The build just hangs forever and fails.

```dockerfile
RUN apt-get install curl       ❌  # Build hangs — waiting for Y/n
RUN apt-get install -y curl    ✓   # Auto-answers YES to everything
```

Think of it like: `-y` = a rubber stamp that automatically approves every question.

---

## 3. `--no-install-recommends` — What is it?

When you install a package, apt-get also installs **recommended** packages (extras it thinks you might want — like documentation, GUI tools, optional plugins).

In Docker, **you almost never need those extras**.

```
apt-get install curl               apt-get install --no-install-recommends curl
        ↓                                          ↓
Installs:                          Installs:
  curl           ✓                   curl          ✓
  ca-certificates (recommended)      (nothing extra)
  libcurl docs    (recommended)
  openssl utils   (recommended)
```

```
Without flag:   Image layer = ~15MB
With flag:      Image layer = ~5MB  ✓ 3x smaller!
```

---

## Full Command — Now It Makes Sense

```dockerfile
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl git \
    && rm -rf /var/lib/apt/lists/*
```

| Part | Purpose |
|---|---|
| `apt-get update` | Refresh package catalog |
| `-y` | Auto-answer YES to prompts |
| `--no-install-recommends` | Skip unnecessary extras |
| `curl git` | The actual packages you want |
| `rm -rf /var/lib/apt/lists/*` | Delete the catalog to save space |

**All three together = smallest possible image layer.** This is the standard pattern you'll see in almost every production Dockerfile.

---

## Q: How to decide when to use shell form and exec form

## Simple Decision Rule

```
Are you writing RUN?        → Shell form
Are you writing CMD or ENTRYPOINT? → Exec form
```

That covers 95% of cases. But let's understand **why**.

---

## What's the actual difference?

```dockerfile
# Shell form — Docker runs this AS:
RUN apt-get install -y curl
→ /bin/sh -c "apt-get install -y curl"
           ↑
       shell is involved

# Exec form — Docker runs this AS:
CMD ["node", "server.js"]
→ node server.js
  ↑
  directly, NO shell involved
```

---

## For `RUN` → Always Shell Form

**Because you NEED the shell features:**

```dockerfile
# You need && to chain
RUN apt-get update && apt-get install -y curl   ✓

# You need \ for line breaks
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*              ✓

# You need | for piping
RUN cat file.txt | grep "something"             ✓

# You need $VAR expansion
RUN echo $HOME                                  ✓
```

None of these work in exec form because **exec form has no shell**.

---

## For `CMD` / `ENTRYPOINT` → Always Exec Form

**Because your app must be PID 1:**

```
Shell form CMD:                    Exec form CMD:
─────────────────                  ─────────────────
PID 1 → /bin/sh                    PID 1 → node  ← your app
PID 2 → node (child)

Problem:                           Result:
Docker sends SIGTERM to PID 1      Signal goes directly to node ✓
/bin/sh gets it                    App shuts down cleanly ✓
/bin/sh may NOT forward it
to node!
App gets killed forcefully ✗
```

**Real impact:**
```dockerfile
CMD node server.js        ❌  # Ctrl+C / docker stop may not work cleanly
CMD ["node", "server.js"] ✓   # Clean shutdown, graceful exit
```

---

## Decision Flowchart

```
What instruction are you writing?
          │
    ┌─────┴──────┐
   RUN          CMD or ENTRYPOINT
    │                   │
Shell form ✓       Exec form ✓
(need &&, |,       (app = PID 1,
 \, $VAR)          clean signals)
```

---

## One Exception — Shell features inside CMD

If you genuinely need shell features in CMD (rare):

```dockerfile
# Need variable expansion in CMD? Use shell form knowingly
CMD echo "Starting on port $PORT"   ← only works in shell form

# Or explicitly call the shell in exec form
CMD ["/bin/sh", "-c", "echo Starting on port $PORT"]
```

But for starting your actual app — always exec form.

---

## Cheat Sheet

| Instruction | Form | Reason |
|---|---|---|
| `RUN` | Shell | Needs `&&`, `\|`, `$VAR` |
| `CMD` | Exec | App must be PID 1 |
| `ENTRYPOINT` | Exec | App must be PID 1 |
| `CMD` (as args to ENTRYPOINT) | Exec | Consistent behavior |

**One sentence to remember:**
> `RUN` builds your image (needs shell power) → shell form. `CMD`/`ENTRYPOINT` starts your app (needs clean signals) → exec form.

---

## Q: But if the parent PID is deleted won't the child also get killed

Excellent question — this is where the real understanding is!

## You're Partially Right — But the Problem is Different

Yes, when PID 1 dies, all children die too. **But that's not the problem.**

The problem is **what happens BEFORE the killing** — the signal journey.

---

## How Docker Stops a Container

When you run `docker stop`, Docker does this:

```
Step 1: Send SIGTERM to PID 1   ← "Please shut down gracefully"
Step 2: Wait 10 seconds
Step 3: If still running → Send SIGKILL   ← "Die NOW, no choice"
```

`SIGTERM` = polite request → app can finish requests, close DB connections, flush logs  
`SIGKILL` = force kill → everything drops immediately, no cleanup

---

## The Shell Form Problem

```
docker stop
    ↓
SIGTERM → sent to PID 1 (/bin/sh)

/bin/sh thinks: "I got SIGTERM"
/bin/sh does NOT forward it to children (by default)
/bin/sh exits

Now node is an ORPHAN — never received SIGTERM
Docker waits 10 seconds...
Docker sends SIGKILL to node
Node dies instantly — no cleanup ✗
```

```
Timeline:
0s  → SIGTERM sent to /bin/sh
0s  → /bin/sh exits (didn't tell node)
10s → Docker loses patience
10s → SIGKILL sent → node force-killed
```

---

## The Exec Form Solution

```
docker stop
    ↓
SIGTERM → sent to PID 1 (node directly)

Node receives SIGTERM
Node thinks: "Okay, let me wrap up..."
Node finishes current requests
Node closes DB connections
Node exits cleanly ✓
```

```
Timeline:
0s  → SIGTERM sent to node
~2s → Node finishes gracefully and exits ✓
      (never needed SIGKILL)
```

---

## Real-world Impact

```javascript
// Node can handle SIGTERM gracefully in exec form:
process.on('SIGTERM', () => {
  console.log('Shutting down gracefully...');
  server.close(() => {          // finish ongoing requests
    db.disconnect();            // close DB connections
    process.exit(0);            // clean exit
  });
});
```

With shell form → this code **never runs** because node never gets SIGTERM.

---

## Summary

```
Shell form:   /bin/sh → (ignores SIGTERM) → node gets SIGKILL after 10s  ✗
Exec form:    node    → (handles SIGTERM) → node exits cleanly in ~2s     ✓
```

Your instinct was right — child dies when parent dies. But the damage is already done in those 10 seconds where the app had **no chance to clean up**.

---

## Q: why you refer shell as /bin/sh?

## `/bin/sh` is just the location of the shell program on Linux

On Linux, everything is a file — including programs. The shell (the thing that understands `&&`, `|`, `$VAR`) lives at a specific path:

```
/          ← root of the filesystem
└── bin/   ← folder for essential programs (binary = executable program)
    ├── sh      ← the shell program  ← this is what /bin/sh means
    ├── ls      ← the list command
    ├── curl    ← the curl command
    └── ...
```

It's the same as on Windows where programs live at:
```
C:\Windows\System32\cmd.exe    ← Windows shell
C:\Program Files\...           ← other programs
```

---

## `sh` vs other shells

`sh` is the most basic, universal shell. There are others:

```
/bin/sh    ← basic shell (always exists, used by Docker)
/bin/bash  ← bash (more features, most popular)
/bin/zsh   ← zsh (what Mac uses by default)
```

Docker uses `/bin/sh` because it's **guaranteed to exist** in almost every Linux image — even tiny Alpine ones.

---

## What Docker actually does with shell form

```dockerfile
RUN apt-get update && apt-get install -y curl
```

Docker internally converts this to:

```
/bin/sh -c "apt-get update && apt-get install -y curl"
  ↑          ↑
  shell    -c means "run this string as a command"
```

So `/bin/sh` is not something special you write — it's what Docker uses **behind the scenes** when you use shell form. That's why I referenced it.

---

## Q: what the note says -> Only one CMD per Dockerfile (last one wins).

## Only one `CMD` per Dockerfile (last one wins)

This means you **can** write multiple `CMD` lines in a Dockerfile — Docker won't give an error. But **only the last one is used**. All previous ones are silently ignored.

---

## Example

```dockerfile
FROM node:20-alpine

CMD ["node", "server.js"]      # ignored ✗
CMD ["node", "app.js"]         # ignored ✗
CMD ["node", "index.js"]       # ← THIS one runs ✓
```

When container starts → only `node index.js` runs.

---

## Why does this even exist as a rule?

Because of **multi-stage builds**. You might have:

```dockerfile
# Stage 1 - builder
FROM node:20-alpine AS builder
CMD ["npm", "run", "build"]    # ignored ✗

# Stage 2 - production
FROM node:20-alpine
CMD ["node", "server.js"]      # ← this wins ✓
```

Each stage can have its own `CMD` — only the final stage's last `CMD` matters.

---

## Common Mistake This Rule Warns About

```dockerfile
FROM node:20-alpine

WORKDIR /app
COPY . .
RUN npm install

CMD ["node", "server.js"]     # you wrote this first
# ... 50 more lines of Dockerfile ...
CMD ["node", "other.js"]      # you forgot you already wrote CMD above
```

Result → `other.js` runs, `server.js` never runs. No warning, no error.

---

## Simple Rule to Follow

> Write `CMD` **once**, at the **very end** of your Dockerfile. That way you never have to think about which one wins.

---

## Q: RUN groupadd -r appgroup && useradd -r -g appgroup appuser — what is -r and -g in this

## Breaking Down the Command

```bash
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
```

Two separate commands chained with `&&`:
1. `groupadd -r appgroup` — create a group
2. `useradd -r -g appgroup appuser` — create a user

---

## `groupadd -r appgroup`

| Part | Meaning |
|---|---|
| `groupadd` | command to create a group |
| `-r` | create a **system group** |
| `appgroup` | name of the group |

---

## `useradd -r -g appgroup appuser`

| Part | Meaning |
|---|---|
| `useradd` | command to create a user |
| `-r` | create a **system user** |
| `-g appgroup` | assign this user to the group `appgroup` |
| `appuser` | name of the user |

---

## What is a "system" user/group (`-r`)?

Linux has two types of users:

```
Regular user:               System user (-r):
─────────────────           ──────────────────
Created for humans          Created for programs/services
Has home directory          No home directory
Can log in                  Cannot log in
UID range: 1000+            UID range: below 1000
Example: john, alice        Example: nginx, mysql, appuser
```

**In Docker context** — your app (`node`, `nginx`, `java`) doesn't need to log in or have a home folder. It just needs to run. So `-r` is the right choice.

```bash
# Without -r → creates a "human-like" user (unnecessary for apps)
useradd appuser        ← gets home dir, login shell, UID 1001...

# With -r → creates a minimal service user (correct for Docker)
useradd -r appuser     ← no home dir, no login shell, UID 999...
```

---

## `-g` — Assign to a Group

```bash
useradd -r -g appgroup appuser
#              ↑
#        "put appuser INTO appgroup"
```

Groups control **permissions**. By putting `appuser` in `appgroup`, you can control exactly what files/folders the app can access:

```dockerfile
RUN groupadd -r appgroup \
    && useradd -r -g appgroup appuser

COPY --chown=appuser:appgroup . /app    # app files owned by appuser:appgroup
USER appuser                            # run as appuser — can access /app ✓
```

---

## Why bother with all this?

Security. In Docker, if you don't specify a user, everything runs as **root**.

```
Running as root:          Running as appuser (-r):
─────────────────         ────────────────────────
If app is hacked →        If app is hacked →
attacker has full         attacker has minimal
root access to            permissions only ✓
the container ✗
```

---

## Q: There is a builder stage and a production and why we are using 2 different base layer

## The Problem First — Without Two Stages

Imagine you're baking bread and delivering it to a customer.

**Do you send the customer your entire kitchen** (oven, flour bags, mixing bowls, dirty utensils) along with the bread?

No — you only send the **finished bread**.

Docker has the same problem:

```dockerfile
# Without multi-stage — everything ends up in final image
FROM node:20-alpine

RUN npm install        # downloads node_modules (heavy! ~200MB)
COPY . .
RUN npm run build      # compiles your code → produces /dist folder

CMD ["node", "dist/server.js"]
```

```
Final image contains:
✓ dist/server.js        ← you NEED this
✗ node_modules/         ← 200MB, NOT needed to run
✗ src/                  ← source code, NOT needed to run
✗ build tools           ← NOT needed to run
✗ npm, compilers        ← NOT needed to run

Total image size: ~500MB  ← bloated!
```

---

## The Solution — Two Stages

```dockerfile
# ---- Stage 1: Builder ----
FROM node:20-alpine AS builder    ← full kitchen

WORKDIR /app
COPY package*.json ./
RUN npm install                   ← install everything (dev tools, compilers)
COPY . .
RUN npm run build                 ← compile → produces /app/dist


# ---- Stage 2: Production ----
FROM node:20-alpine AS production ← fresh, clean kitchen

WORKDIR /app
COPY --from=builder /app/dist ./  ← take ONLY the bread (compiled output)

CMD ["node", "server.js"]
```

```
Stage 1 (builder):              Stage 2 (production):
────────────────────            ─────────────────────
node_modules/   ✓               dist/server.js  ✓  ← only this survives
src/            ✓               
dist/           ✓ ──copied──►
build tools     ✓               
npm, compilers  ✓               

Size: ~500MB                    Size: ~80MB  ✓
```

**Stage 1 is thrown away after the build. Only Stage 2 becomes your final image.**

---

## Why Two DIFFERENT Base Images?

Look at this example more carefully:

```dockerfile
# Stage 1 — needs full Node.js to BUILD
FROM node:20-alpine AS builder
RUN npm install
RUN npm run build


# Stage 2 — only needs to RUN the compiled output
FROM node:20-alpine AS production
```

In this case both use the same base. But consider a **Java app**:

```dockerfile
# Stage 1 — needs JDK (Java Development Kit) to COMPILE
FROM maven:3.9-eclipse-temurin-17 AS builder
RUN mvn package                    ← compiles .java → .jar


# Stage 2 — only needs JRE (Java Runtime) to RUN
FROM eclipse-temurin:17-jre AS production
COPY --from=builder /app/target/app.jar .
CMD ["java", "-jar", "app.jar"]
```

```
JDK (builder):              JRE (production):
────────────────            ──────────────────
Java compiler    ✓          Java compiler  ✗ (not needed)
javac            ✓          javac          ✗ (not needed)
maven/gradle     ✓          maven          ✗ (not needed)
JRE (to run)     ✓          JRE (to run)   ✓

Size: ~600MB                Size: ~200MB  ✓
```

**JDK** = tools to write + compile + run Java  
**JRE** = tools to ONLY run Java (no compiler)

---

## Visual Summary

```
docker build .
      │
      ▼
┌─────────────────────┐
│   Stage 1: builder  │  "The Kitchen"
│   - install tools   │  Heavy, messy, lots of stuff
│   - compile code    │  NEVER shipped to users
│   - run tests       │
└────────┬────────────┘
         │  COPY only the finished output
         ▼
┌─────────────────────┐
│  Stage 2: production│  "The Delivery Box"
│  - just the output  │  Lean, clean, small
│  - minimal tools    │  THIS is what gets shipped ✓
└─────────────────────┘
         │
         ▼
   Final Docker Image  ← small, secure, production-ready
```

---

## One Line Summary

> Stage 1 = build your app (needs all tools). Stage 2 = run your app (needs nothing extra). You copy only the result from Stage 1 into Stage 2 — everything else is discarded.

---

## Q: when you say throw away stage 1 that means it is discarded when we run after build

Exactly right! Let me show you precisely when it gets discarded.

---

## Timeline of What Happens

```
docker build .
      │
      ▼
Stage 1 (builder) runs...
  - npm install        ✓
  - npm run build      ✓
  - /app/dist created  ✓
      │
      │  COPY --from=builder /app/dist ./
      │  (copies dist into Stage 2)
      ▼
Stage 2 (production) runs...
  - receives /app/dist ✓
      │
      ▼
Build finishes ← Stage 1 is GONE at this point
      │
      ▼
docker run my-app  ← only Stage 2 exists here
```

**Stage 1 is discarded at the END of `docker build`** — before you even run the container.

---

## Proof — Check Image Size Yourself

```bash
docker build -t my-app .
docker images my-app
```

```
REPOSITORY   TAG      IMAGE ID       SIZE
my-app       latest   abc123def456   85MB   ← only Stage 2 size
```

You will NOT see a 500MB image. Stage 1 never becomes part of the final image.

---

## Does Stage 1 Exist Anywhere After Build?

```
Stage 1 during build:    ✓ exists temporarily in memory/cache
Stage 1 after build:     ✗ not in your final image
Stage 1 when container
runs (docker run):        ✗ completely gone
```

Docker does keep it in **build cache** (to speed up future builds), but:
- It is NOT part of your image
- It is NOT shipped anywhere
- It does NOT run when you do `docker run`

---

## Simple Analogy

Think of building a house:

```
Stage 1 (builder) = Construction site
  - scaffolding, cranes, cement mixers
  - workers, tools, blueprints

Stage 2 (production) = The finished house
  - only walls, rooms, doors

When you move into the house (docker run):
  - scaffolding is gone ✓
  - cranes are gone ✓
  - only the house remains ✓
```

`docker run` = moving into the finished house. The construction equipment was never part of your home.

---

## Q: RUN addgroup -S appgroup && adduser -S appuser -G appgroup — I don't understand this part I understood -r is to create system user right but what is -S and appgroup I don't understand what is this appgroup how is the appuser. what will be the of appgroup and appuser etc

## Breaking It Down Piece by Piece

```bash
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
```

This is **Alpine Linux** syntax (used in `node:20-alpine`). It's two commands:
1. `addgroup -S appgroup` — create a group
2. `adduser -S appuser -G appgroup` — create a user and put them in that group

---

### What is `appgroup` and `appuser`?

**They are just names. You made them up. You can call them anything.**

```bash
addgroup -S appgroup     # "appgroup" is just the name I chose
addgroup -S mygroup      # this works too
addgroup -S nodejs       # this works too
addgroup -S banana       # even this works ✓ (just a bad name)
```

Same for `appuser`:
```bash
adduser -S appuser       # "appuser" is just the name I chose
adduser -S noderunner    # this works too
adduser -S myapp         # this works too
```

Convention is to use `appgroup` / `appuser` so anyone reading the Dockerfile understands their purpose.

---

### What is `-S`?

`-S` in Alpine = same as `-r` in Ubuntu. It means **system user/group**.

```
Ubuntu/Debian:    groupadd -r     =    Alpine: addgroup -S
Ubuntu/Debian:    useradd  -r     =    Alpine: adduser  -S
```

Both mean: "this is for a service/program, not a human"

```
System user (-S):           Regular user:
──────────────────          ──────────────
No home directory           Has /home/username
Cannot log in               Can log in
UID below 1000              UID 1000+
For apps/services           For humans
```

---

### What is a Group and Why Do We Need It?

Think of a **group** like a department in a company:

```
Company (Linux):
├── HR Department (hr-group)           → can access /hr-files/
├── Finance Department (finance-group) → can access /finance-files/
└── App Department (appgroup)          → can access /app/
```

A group controls **who can access what files**.

```
Without group:    appuser has no department → unclear permissions
With group:       appuser belongs to appgroup → clean permission control
```

---

### What Actually Gets Created?

```bash
addgroup -S appgroup
```
Creates an entry in `/etc/group`:
```
appgroup:x:998:     ← group name : password : group ID : members
```

```bash
adduser -S appuser -G appgroup
```
Creates an entry in `/etc/passwd`:
```
appuser:x:999:998::/home/appuser:/sbin/nologin
#  ↑         ↑  ↑       ↑              ↑
# name      UID GID   home dir     can't log in
```

---

### The Full Picture in Dockerfile

```dockerfile
# Step 1: Create a group called appgroup
RUN addgroup -S appgroup \

# Step 2: Create a user called appuser, put them in appgroup
    && adduser -S appuser -G appgroup

# Step 3: Give your app files to appuser:appgroup
COPY --chown=appuser:appgroup . /app

# Step 4: Switch to appuser — container now runs as this user
USER appuser

# Step 5: Start the app — runs as appuser, NOT root
CMD ["node", "server.js"]
```

```
Who runs the app?    appuser  (limited permissions ✓)
What can they touch? only /app (because of --chown ✓)
Can they log in?     NO (-S flag ✓)
Can they break out?  Much harder than root ✓
```

---

### One Sentence Summary

> `appgroup` and `appuser` are **names you choose** for a locked-down service account. `-S` makes them system-level (no login, no home dir). `-G appgroup` puts the user inside the group so file permissions work cleanly.

---

## Q: I can see 22 layers created in docker desktop UI, can you explain each of them

These 22 layers split into two groups — layers that came from the **base image** (`node:20-alpine`) and layers **you created** in your Dockerfile.

---

## Layers 0–8 — From `node:20-alpine` (NOT yours)

These already existed inside the base image before you wrote a single line.

| Layer | Instruction | Size | What it does |
|---|---|---|---|
| 0 | `ADD alpine-minirootfs-3.23.4-x86_64.tar.gz /` | 8.45 MB | Extracts Alpine Linux OS — the bare minimum Linux filesystem |
| 1 | `CMD ["/bin/sh"]` | 0 B | Alpine's default shell — just metadata, no files |
| 2 | `ENV NODE_VERSION=20.20.2` | 0 B | Records Node version — just metadata |
| 3 | `RUN /bin/sh -c addgroup... curl... node-v...` | 121.87 MB | **The big one** — downloads, compiles, installs Node.js 20 |
| 4 | `ENV YARN_VERSION=1.22.22` | 0 B | Records Yarn version — just metadata |
| 5 | `RUN /bin/sh -c apk add... yarn...` | 5.36 MB | Installs Yarn package manager |
| 6 | `COPY docker-entrypoint.sh` | 388 B | A startup script that sets up the node user properly |
| 7 | `ENTRYPOINT ["docker-entrypoint.sh"]` | 0 B | Sets that startup script as entrypoint — metadata |
| 8 | `CMD ["node"]` | 0 B | Base image default: run `node` — metadata |

> **All of this is baked into `FROM node:20-alpine`** — you get it for free when you choose that base image.

---

## Layers 9–21 — From YOUR Dockerfile

| Layer | Your Instruction | Size | What it does |
|---|---|---|---|
| 9 | `LABEL maintainer=gk@example.com` | 0 B | Metadata only — no files written |
| 10 | `LABEL version=1.0.0` | 0 B | Metadata only — no files written |
| 11 | `WORKDIR /app` | 0 B | Creates `/app` folder, sets it as working dir |
| 12 | `COPY package.json .` | 84 B | Copies your package.json into `/app` |
| 13 | `RUN npm install` | 1.06 KB | Installs dependencies (tiny because no dependencies in package.json) |
| 14 | `COPY server.js .` | 267 B | Copies your server.js into `/app` |
| 15 | `ENV NODE_ENV=production` | 0 B | Sets environment variable — metadata |
| 16 | `ENV PORT=3000` | 0 B | Sets environment variable — metadata |
| 17 | `EXPOSE 3000/tcp` | 0 B | Documents port — metadata only |
| 18 | `HEALTHCHECK {...}` | 0 B | Registers health check command — metadata |
| 19 | `RUN addgroup -S appgroup && adduser -S appuser` | 3.22 KB | Creates the group and user entries in `/etc/group` and `/etc/passwd` |
| 20 | `USER appuser` | 0 B | Switches active user — metadata |
| 21 | `CMD ["node" "server.js"]` | 0 B | Sets default start command — metadata |

---

## Key Observations

**Why are most layers `0 B`?**
```
ENV, LABEL, EXPOSE, USER, CMD, ENTRYPOINT, HEALTHCHECK, WORKDIR
→ These only write metadata into the image config
→ They create NO new files on disk = 0 B
```

**Only these instructions create actual file data:**
```
ADD / COPY   → adds files
RUN          → creates/modifies files during execution
```

**Size breakdown of YOUR image:**
```
Base (node:20-alpine):   ~135MB  (layers 0–8, you inherited this)
Your additions:          ~5KB    (layers 9–21, your actual app)
```

> This is exactly why we use `node:20-alpine` instead of `node:20` — the full version would add ~800MB just for layers 0–8.

---

## Q: How can i test port 3000 inside the container

First check if your container is running:

```bash
docker ps
```

You have two ways to test port 3000:

---

### Option 1 — From outside (your Windows machine)

Port 3000 inside the container is mapped to port 8080 on your machine (`8080->3000`). So test it from your PC:

```powershell
curl http://localhost:8080
```

---

### Option 2 — From inside the container (directly on port 3000)

```bash
docker exec my-container wget -qO- http://localhost:3000
```

```
Your PC                        Container
────────────────               ──────────────────
localhost:8080  ──port map──►  localhost:3000  ← app listens here
     ↑                               ↑
 test from                      test from
 outside                        inside (docker exec)
```

**`docker exec`** — lets you run any command inside a running container without going into it. Useful for quick checks.

To go fully inside the container and explore:
```bash
docker exec -it my-container sh
# now you're inside — try:  wget -qO- http://localhost:3000
# type exit to come back out
```

---

## Q: docker exec -it my-container sh and wget -qO- http://localhost:3000 — explain me these codes

## `docker exec -it my-container sh`

Breaking it into parts:

```
docker exec        → run a command inside an ALREADY running container
-it                → two flags combined
  -i               → interactive  (keep input open — so you can type)
  -t               → tty          (give me a terminal — so it looks like a real shell)
my-container       → name of the container to go inside
sh                 → the command to run inside (open a shell)
```

**What happens:**
```
Your PC terminal              Inside container
────────────────              ──────────────────
$ docker exec -it ...
                    ────────► /app $ _    ← you are now HERE
                              (Alpine Linux shell)
```

You're literally sitting inside the container now — like SSH-ing into a remote machine.

---

## `wget -qO- http://localhost:3000`

```
wget              → a tool to fetch content from a URL (like curl)
-q                → quiet mode — don't show download progress, just output
-O-               → Output to screen instead of saving to a file
                    (capital O, then dash — "dash" means stdout)
http://localhost:3000  → the URL to fetch
                        localhost = this container itself
                        3000 = the port your app listens on
```

**Why `localhost` works from inside:**

```
From your PC:          localhost = your Windows machine
                       port 3000 is NOT open on your PC
                       → must use localhost:8080 (mapped port)

From inside container: localhost = the container itself
                       port 3000 IS open here (app runs here)
                       → localhost:3000 works directly ✓
```

---

## Why `-it` together?

```bash
docker exec my-container sh        # opens shell but you can't type — freezes
docker exec -i my-container sh     # can type but looks broken — no proper terminal
docker exec -it my-container sh    # full interactive terminal ✓
```

Think of it like:
- `-i` = connect your keyboard
- `-t` = connect your screen
- Together = full working terminal session

---

## Full flow

```bash
# 1. Enter the container
docker exec -it my-container sh

# 2. Now you're inside — prompt changes to:
/app $

# 3. Test the app from inside
/app $ wget -qO- http://localhost:3000
Hello from Docker! 🐳

# 4. Exit the container (back to your PC)
/app $ exit
$
```

> `docker exec` is one of the most useful debugging tools in Docker — whenever something is wrong inside a container, you `exec` in and investigate like a detective.
