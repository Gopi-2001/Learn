# Module 10 — Horizontal Pod Autoscaler (HPA)

## What is HPA?

HPA automatically **scales the number of pod replicas** based on observed CPU or memory usage.

```
Low traffic  → 2 pods
High traffic → K8s adds pods automatically (up to max)
              → K8s removes pods when traffic drops
```

This is the Kubernetes equivalent of "auto-scaling" in cloud platforms.

---

## Prerequisites

HPA needs the **Metrics Server** to read CPU/memory from pods.

```powershell
# Enable metrics server in minikube
minikube addons enable metrics-server

# Verify it's running (takes 1-2 min)
kubectl top nodes
kubectl top pods
```

---

## Deploy and Apply HPA

```powershell
# Apply the deployment (from Module 03)
kubectl apply -f ../03-deployments/deployment.yaml

# Apply the HPA
kubectl apply -f hpa.yaml

# Check HPA status
kubectl get hpa
# NAME           REFERENCE                TARGETS   MINPODS   MAXPODS   REPLICAS
# hello-java-hpa Deployment/hello-java   2%/50%    2         10        2
```

---

## Load Test — Watch Auto-Scaling in Action

```powershell
# Terminal 1: Watch pods
kubectl get pods -w

# Terminal 2: Generate load
kubectl run load-generator --image=busybox --restart=Never -- \
  sh -c "while true; do wget -qO- http://hello-java-cluster:8080/; done"

# Wait 1-2 minutes, then check HPA
kubectl get hpa
# TARGETS will go up → K8s adds pods automatically

# Stop the load generator
kubectl delete pod load-generator

# Wait ~5 minutes — K8s scales back down
kubectl get hpa -w
```

---

## HPA Key Concepts

- `minReplicas` — never go below this (keeps the app available)
- `maxReplicas` — never go above this (cost control)
- `targetCPUUtilizationPercentage: 50` — scale up when average CPU > 50%
- Scale-up is fast (seconds); scale-down is slow (default: 5 min — prevents flapping)

---

## Clean Up

```powershell
kubectl delete -f hpa.yaml
kubectl delete -f ../03-deployments/deployment.yaml
```

---

## Next Step

→ [../11-resource-limits/01-resources.md](../11-resource-limits/01-resources.md) — Resource requests and limits
