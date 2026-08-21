# Module 11 — Resource Requests & Limits

## Why Resource Management Matters

Without resource limits:
- One pod uses all CPU → other pods starve
- One pod uses all memory → node OOMKills other pods
- HPA has no data to make scaling decisions

---

## Requests vs Limits

```
requests:           What the pod is GUARANTEED to get
                    Used by the Scheduler to find a node with enough capacity

limits:             The MAXIMUM the pod can use
                    If exceeded: CPU is throttled, Memory causes OOMKill
```

```
Node capacity: 4 CPU, 8GB RAM
─────────────────────────────────────────────────────────────
Pod A: requests 500m CPU, 256Mi RAM   → guaranteed
Pod B: requests 200m CPU, 128Mi RAM   → guaranteed
Pod C: requests 1000m CPU, 512Mi RAM  → guaranteed
─────────────────────────────────────────────────────────────
Total requested: 1700m CPU, 896Mi RAM  (fits on the node)
```

---

## CPU Units

```
1 CPU   = 1 vCPU = 1000m (millicores)
500m    = 0.5 CPU
100m    = 0.1 CPU (10% of one CPU)
250m    = 0.25 CPU
```

## Memory Units

```
128Mi  = 128 mebibytes (≈ 134 MB)
256Mi  = 256 mebibytes
512Mi  = 512 mebibytes
1Gi    = 1 gibibyte (≈ 1.07 GB)
```

---

## Spring Boot Recommendations

Spring Boot JVM needs more memory than Node.js:

```yaml
# Development / learning
resources:
  requests:
    memory: "256Mi"
    cpu: "100m"
  limits:
    memory: "512Mi"
    cpu: "500m"

# Production (tuned for your workload)
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

---

## Apply and Observe

```powershell
kubectl apply -f deployment-with-resources.yaml

# Watch resource usage (requires metrics-server)
kubectl top pods
# NAME                     CPU(cores)   MEMORY(bytes)
# hello-java-xyz           45m          180Mi

kubectl top nodes
# NAME       CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%
# minikube   400m         10%    1500Mi          40%
```

---

## Resource Quota per Namespace (bonus)

You can limit the total resources a namespace can consume:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: myapp-quota
  namespace: myapp
spec:
  hard:
    requests.cpu: "2"          # total CPU requests in namespace ≤ 2 cores
    requests.memory: 2Gi       # total memory requests ≤ 2Gi
    limits.cpu: "4"
    limits.memory: 4Gi
    pods: "20"                 # max 20 pods in this namespace
```

---

## Next Step

→ [../12-rolling-updates/01-rolling-updates.md](../12-rolling-updates/01-rolling-updates.md) — Rolling updates and rollbacks
