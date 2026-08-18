# Kubernetes Learning Path — Java Edition
## From Basics to Advanced (with Hands-On)

---

## Prerequisites
- ✅ You know Docker (my-first-java-docker-app)
- ✅ You know Docker Compose (my-first-java-compose-app)
- ✅ Java / Spring Boot basics

---

## Learning Roadmap

```
Docker              Docker Compose          Kubernetes
─────────           ──────────────          ──────────────────────────────────
1 container         Multiple containers     Cluster of containers
on 1 machine        on 1 machine            across many machines

docker run          docker compose up       kubectl apply -f manifest.yaml
Dockerfile          docker-compose.yml      Pod / Deployment / Service YAML
```

---

## What You'll Build

A Java Spring Boot app, progressively evolved:

| Module | What you do | New K8s concept |
|---|---|---|
| 01 | Setup minikube + kubectl | Cluster, Node |
| 02 | Run a single Pod | Pod |
| 03 | Add self-healing + scaling | Deployment, ReplicaSet |
| 04 | Expose the app | Service (ClusterIP, NodePort) |
| 05 | Externalize config | ConfigMap, Secret |
| 06 | Organize resources | Namespace |
| 07 | Add persistent storage | PersistentVolume, PVC |
| 08 | Route external traffic | Ingress |
| 09 | Full stack (Java+PG+Redis) | StatefulSet, full deployment |
| 10 | Auto-scale | HPA |
| 11 | Control resources | Resource requests & limits |
| 12 | Deploy updates safely | Rolling update, Rollback |
| 13 | Package with Helm | Helm chart |

---

## Module Index

1. [01-setup/01-what-is-kubernetes.md](01-setup/01-what-is-kubernetes.md) — Theory: K8s architecture
2. [01-setup/02-install-minikube.md](01-setup/02-install-minikube.md) — Install minikube + kubectl
3. [01-setup/03-kubectl-basics.md](01-setup/03-kubectl-basics.md) — Essential kubectl commands
4. [02-pods/01-pods.md](02-pods/01-pods.md) — Your first Pod
5. [03-deployments/01-deployments.md](03-deployments/01-deployments.md) — Deployments & ReplicaSets
6. [04-services/01-services.md](04-services/01-services.md) — Services & networking
7. [05-configmaps-secrets/01-configmaps-secrets.md](05-configmaps-secrets/01-configmaps-secrets.md) — Config & Secrets
8. [06-namespaces/01-namespaces.md](06-namespaces/01-namespaces.md) — Namespaces
9. [07-persistent-storage/01-persistent-storage.md](07-persistent-storage/01-persistent-storage.md) — Volumes & PVCs
10. [08-ingress/01-ingress.md](08-ingress/01-ingress.md) — Ingress controller
11. [09-full-stack/01-full-stack-guide.md](09-full-stack/01-full-stack-guide.md) — Full stack on K8s
12. [10-scaling/01-hpa.md](10-scaling/01-hpa.md) — Horizontal Pod Autoscaler
13. [11-resource-limits/01-resources.md](11-resource-limits/01-resources.md) — Resource management
14. [12-rolling-updates/01-rolling-updates.md](12-rolling-updates/01-rolling-updates.md) — Rolling updates & rollbacks
15. [13-helm/01-helm-intro.md](13-helm/01-helm-intro.md) — Helm charts

---

## The App We Use

We use the Java app from `my-first-java-docker-app`. Build it once:

```powershell
# In my-first-java-docker-app directory:
docker build -t hello-java:1.0.0 .

# Load into minikube's Docker daemon (so K8s can find it):
minikube image load hello-java:1.0.0
```

---

## Docker vs Kubernetes Mental Model

```
Docker Compose                    Kubernetes
──────────────────────────────    ──────────────────────────────────────
docker-compose.yml                Multiple .yaml manifest files
service:                          Deployment + Service (separate objects)
  image: hello-java               spec.containers[].image: hello-java
  ports: - "8080:8080"            Service type: NodePort / LoadBalancer
  environment: KEY=val            ConfigMap / Secret
  volumes: ./data:/data           PersistentVolumeClaim
  restart: unless-stopped         spec.restartPolicy (always in Deployments)
  depends_on: db                  readinessProbe (K8s doesn't have depends_on)
  networks: backend               Labels + Selectors
  healthcheck: ...                livenessProbe + readinessProbe
```
