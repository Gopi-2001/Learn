# Module 01 — Install Minikube & kubectl

## What is Minikube?

Minikube runs a **single-node Kubernetes cluster** on your local machine inside a VM or Docker container. It's the easiest way to learn K8s without needing cloud infrastructure.

```
Your Machine
└── Minikube (single node acts as both Control Plane + Worker)
    └── Kubernetes cluster
        └── Your Pods / Deployments / Services
```

---

## Step 1 — Install kubectl

`kubectl` is the CLI you use to talk to any Kubernetes cluster (local or cloud).

### Windows (PowerShell — run as Administrator)

```powershell
# Option A: via winget (recommended)
winget install -e --id Kubernetes.kubectl

# Option B: via Chocolatey
choco install kubernetes-cli

# Option C: manual download
curl.exe -LO "https://dl.k8s.io/release/v1.29.0/bin/windows/amd64/kubectl.exe"
# Move kubectl.exe to a folder in your PATH (e.g., C:\Windows\System32)

# Verify
kubectl version --client
```

---

## Step 2 — Install Minikube

```powershell
# Option A: via winget
winget install -e --id Kubernetes.minikube

# Option B: via Chocolatey
choco install minikube

# Option C: manual download from https://minikube.sigs.k8s.io/docs/start/

# Verify
minikube version
```

---

## Step 3 — Start Your Cluster

```powershell
# Start minikube using Docker as the driver
# (you already have Docker installed from your Docker learning!)
minikube start --driver=docker

# Expected output:
# 😄  minikube v1.32.0 on Windows 10
# ✨  Using the docker driver based on user configuration
# 🏄  Done! kubectl is now configured to use "minikube" cluster
```

---

## Step 4 — Verify Everything Works

```powershell
# Check cluster info
kubectl cluster-info

# See the single node (your minikube VM)
kubectl get nodes
# NAME       STATUS   ROLES           AGE   VERSION
# minikube   Ready    control-plane   1m    v1.29.0

# See all system pods running inside K8s
kubectl get pods -n kube-system
```

---

## Minikube Cheat Sheet

```powershell
minikube start              # start cluster
minikube stop               # stop cluster (keeps your work)
minikube delete             # delete cluster (wipe everything)
minikube status             # check if cluster is running
minikube dashboard          # open K8s web UI in browser
minikube ip                 # get the cluster IP (for NodePort access)

# Load a locally built Docker image into minikube
# (so K8s can use images you built with 'docker build' locally)
minikube image load hello-java:1.0.0

# SSH into the minikube node
minikube ssh
```

---

## Important: Loading Local Docker Images

When you run `docker build -t hello-java:1.0.0 .`, the image is in your **local** Docker daemon. Minikube has its **own** Docker daemon — it can't see your local images by default.

Two solutions:

**Option A — minikube image load** (used in this guide):
```powershell
# Build normally
docker build -t hello-java:1.0.0 .

# Load into minikube
minikube image load hello-java:1.0.0
```

**Option B — build inside minikube's Docker daemon**:
```powershell
# Point your shell's Docker CLI at minikube's Docker daemon
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Now docker build goes directly into minikube
docker build -t hello-java:1.0.0 .

# Revert to local Docker daemon when done
& minikube docker-env -u --shell powershell | Invoke-Expression
```

All YAML manifests in this guide use `imagePullPolicy: Never` so K8s uses the pre-loaded local image.

---

## Next Step

→ [03-kubectl-basics.md](03-kubectl-basics.md) — Essential kubectl commands
