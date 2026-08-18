# Module 01 — What is Kubernetes?

## The Problem Docker Compose Doesn't Solve

Docker Compose is great for **one machine**. But in production:

- What if that machine crashes? ❌ App is down.
- What if traffic spikes? ❌ You can't auto-scale.
- What if you want zero-downtime deploys? ❌ Hard.
- What if you have 50 microservices? ❌ Compose gets complex.

**Kubernetes (K8s)** solves all of this. It's a **container orchestrator** that manages containers across a **cluster of machines**.

---

## Kubernetes Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        CLUSTER                               │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              CONTROL PLANE (Master)                     │ │
│  │                                                         │ │
│  │  ┌──────────────┐  ┌───────────┐  ┌─────────────────┐  │ │
│  │  │ API Server   │  │ Scheduler │  │ Controller Mgr  │  │ │
│  │  │ (front door) │  │ (places   │  │ (ensures desired│  │ │
│  │  │              │  │  pods)    │  │  state matches) │  │ │
│  │  └──────────────┘  └───────────┘  └─────────────────┘  │ │
│  │                                                         │ │
│  │  ┌──────────────────────────────────────────────────┐   │ │
│  │  │               etcd (key-value store)             │   │ │
│  │  │          Stores all cluster state                │   │ │
│  │  └──────────────────────────────────────────────────┘   │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌─────────────────┐  ┌─────────────────┐                   │
│  │    WORKER NODE  │  │    WORKER NODE  │  ...              │
│  │                 │  │                 │                   │
│  │  ┌───┐  ┌───┐   │  │  ┌───┐  ┌───┐   │                  │
│  │  │Pod│  │Pod│   │  │  │Pod│  │Pod│   │                  │
│  │  └───┘  └───┘   │  │  └───┘  └───┘   │                  │
│  │  kubelet         │  │  kubelet         │                  │
│  │  kube-proxy      │  │  kube-proxy      │                  │
│  └─────────────────┘  └─────────────────┘                   │
└──────────────────────────────────────────────────────────────┘
         ↑
         │  you talk to the API Server via kubectl
```

### Key Components

| Component | Role | Analogy |
|---|---|---|
| **API Server** | Entry point for all K8s operations | Compose's docker daemon |
| **Scheduler** | Decides which node runs each Pod | Docker Compose's `depends_on` (smarter) |
| **Controller Manager** | Ensures desired state = actual state | Docker's restart policy (smarter) |
| **etcd** | Distributed key-value store for cluster state | docker-compose.yml (source of truth) |
| **kubelet** | Agent on each node; starts/stops containers | Docker Engine on a machine |
| **kube-proxy** | Network rules on each node | Docker Compose networks |

---

## Key Kubernetes Objects

```
Namespace       → logical group of resources (like a folder)
Pod             → one or more containers running together (smallest unit)
ReplicaSet      → ensures N identical Pods are always running
Deployment      → manages ReplicaSets; enables rolling updates (use this, not raw Pods)
Service         → stable network endpoint to reach Pods (Pods have dynamic IPs)
ConfigMap       → non-sensitive config injected as env vars or files
Secret          → sensitive config (passwords) — base64 encoded
PersistentVolume (PV) → actual storage (disk)
PersistentVolumeClaim (PVC) → a request for storage (your app asks for PV)
Ingress         → HTTP routing rules (path /api → service A, /web → service B)
HPA             → Horizontal Pod Autoscaler (auto-scale based on CPU/memory)
StatefulSet     → like Deployment but for stateful apps (databases)
```

---

## Kubernetes vs Docker Compose — The Mental Shift

```
Docker Compose              Kubernetes
──────────────────────      ────────────────────────────────────
Single file                 Many YAML files (one per object type)
Single machine              Many machines (cluster)
service: api                Deployment named "api"
  image: hello-java           spec.containers[0].image: hello-java
  ports: "8080:8080"        Service type: NodePort
  environment: KEY=val      ConfigMap or Secret
  depends_on: db            readinessProbe (waits until ready)
  healthcheck: ...          livenessProbe + readinessProbe
  restart: unless-stopped   spec.restartPolicy: Always (default)
  volumes: ./data:/data     PersistentVolumeClaim
```

---

## The Desired State Model

Kubernetes is **declarative**: you declare what you **want**, and K8s figures out how to get there.

```
You say:   "I want 3 replicas of hello-java"

           kubectl apply -f deployment.yaml

K8s does:  [checks current state: 0 pods]
           → starts Pod 1 on Node A
           → starts Pod 2 on Node B
           → starts Pod 3 on Node A

Node A crashes:
K8s does:  → detects Pod 1 and Pod 3 are gone
           → starts new Pod 1 and Pod 3 on Node B
           → desired state (3 pods) restored automatically
```

This is fundamentally different from Docker Compose where you manually intervene.

---

## Next Step

→ [02-install-minikube.md](02-install-minikube.md) — Set up a local Kubernetes cluster
