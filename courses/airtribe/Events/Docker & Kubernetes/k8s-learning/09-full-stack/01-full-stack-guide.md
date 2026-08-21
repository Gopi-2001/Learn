# Module 09 — Full Stack on Kubernetes
## Java Spring Boot + PostgreSQL + Redis + Ingress

This is the Kubernetes equivalent of **my-first-java-compose-app**.

---

## Architecture

```
Internet
   │
   ▼
┌──────────────────────────────────────────────────────────────┐
│  Namespace: javaapp                                          │
│                                                              │
│  Ingress (hello-java.local)                                  │
│      │                                                       │
│      ▼                                                       │
│  ┌─────────────────────────────────┐                         │
│  │  Deployment: java-api           │  (3 replicas)           │
│  │  Spring Boot + JPA + Redis      │                         │
│  └─────────────────────────────────┘                         │
│         │                    │                               │
│         ▼                    ▼                               │
│  ┌──────────────┐    ┌──────────────┐                        │
│  │ StatefulSet  │    │ Deployment   │                        │
│  │ postgres-0   │    │ redis        │                        │
│  │ (+ PVC 1Gi)  │    │              │                        │
│  └──────────────┘    └──────────────┘                        │
└──────────────────────────────────────────────────────────────┘
```

---

## Step 1 — Prepare: Build and Load the Java Image

```powershell
# Build the Java compose app image
cd c:\Learn\copilot\my-first-java-compose-app
docker build -f api/Dockerfile -t java-compose-api:1.0.0 .

# Load into minikube
minikube image load java-compose-api:1.0.0

# Verify
minikube image ls | findstr java-compose
```

---

## Step 2 — Enable Ingress (if not done in Module 08)

```powershell
minikube addons enable ingress
```

---

## Step 3 — Deploy Everything

Apply the manifests in order (dependencies first):

```powershell
cd c:\Learn\copilot\k8s-learning\09-full-stack

# 1. Create namespace
kubectl apply -f 01-namespace.yaml

# 2. Create Secrets (DB password, Redis password)
kubectl apply -f 02-secrets.yaml

# 3. Create ConfigMaps (DB host, port, name, Redis host)
kubectl apply -f 03-configmap.yaml

# 4. Create PVC for PostgreSQL data
kubectl apply -f 04-postgres-pvc.yaml

# 5. Deploy PostgreSQL
kubectl apply -f 05-postgres-statefulset.yaml

# 6. Deploy Redis
kubectl apply -f 06-redis-deployment.yaml

# 7. Deploy Java API (after DB and Redis are ready)
kubectl apply -f 07-java-api-deployment.yaml

# 8. Create Ingress
kubectl apply -f 08-ingress.yaml
```

Or apply all at once:
```powershell
kubectl apply -f . -n javaapp
```

---

## Step 4 — Watch Everything Come Up

```powershell
# Watch pods start
kubectl get pods -n javaapp -w

# Expected (all Running):
# NAME                        READY   STATUS    RESTARTS
# java-api-6d9fb-abc12        1/1     Running   0
# java-api-6d9fb-def34        1/1     Running   0
# java-api-6d9fb-ghi56        1/1     Running   0
# postgres-0                  1/1     Running   0
# redis-5c8d7-jkl78           1/1     Running   0
```

---

## Step 5 — Add Hosts Entry and Test

```powershell
# Get minikube IP
minikube ip

# Add to C:\Windows\System32\drivers\etc\hosts (run as admin):
# <minikube-ip> javaapp.local

# Test endpoints
curl http://javaapp.local/
curl http://javaapp.local/          # 2nd call → from Redis cache
curl http://javaapp.local/visits    # all visits
curl http://javaapp.local/health
```

---

## Step 6 — Explore

```powershell
# See all resources in the namespace
kubectl get all -n javaapp

# Check the Java API logs
kubectl logs -l app=java-api -n javaapp --follow

# Check PostgreSQL
kubectl exec -it postgres-0 -n javaapp -- psql -U appuser -d appdb
# \dt           → list tables
# SELECT * FROM visits;

# Check Redis
kubectl exec -it deploy/redis -n javaapp -- redis-cli
# KEYS *
# GET "visits:/"
```

---

## Cleanup

```powershell
# Delete everything in the namespace at once
kubectl delete namespace javaapp
```
