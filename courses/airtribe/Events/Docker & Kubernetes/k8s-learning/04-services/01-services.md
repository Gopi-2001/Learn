# Module 04 — Services

## The Problem: Pods Have Dynamic IPs

Every time a Pod is created, it gets a **new IP address**. You can't hardcode pod IPs.

```
Pod A (IP: 10.0.0.5)  →  crashes  →  Pod A' (IP: 10.0.0.12)  ← different IP!
```

**Services** solve this: a Service provides a **stable IP and DNS name** that always routes to healthy pods.

---

## Service Types

```
ClusterIP    → Internal only. Reachable from inside the cluster. (default)
NodePort     → Opens a port on every node. External traffic → NodePort → Pods.
LoadBalancer → Creates a cloud load balancer. (AWS/GCP/Azure only)
ExternalName → Maps a service to an external DNS name.
```

For local development with minikube: use **NodePort** or `kubectl port-forward`.

---

## How Services Find Pods — Labels & Selectors

```
Service                              Pods
────────────────────────────────     ─────────────────────────────────
spec:                                metadata:
  selector:                            labels:
    app: hello-java       ←──────────    app: hello-java
                                      ─────────────────────────────────
                                      metadata:
                                        labels:
                                          app: hello-java    ← matched!
```

The Service sends traffic to **all pods** that match its `selector`. This is how load balancing works.

---

## Apply Services

```powershell
# First ensure the deployment is running
kubectl apply -f ../03-deployments/deployment.yaml

# Apply both services
kubectl apply -f clusterip-service.yaml
kubectl apply -f nodeport-service.yaml

# Check
kubectl get services
# NAME                TYPE        CLUSTER-IP      PORT(S)          AGE
# kubernetes          ClusterIP   10.96.0.1       443/TCP          5d
# hello-java-cluster  ClusterIP   10.100.200.50   8080/TCP         1m
# hello-java-node     NodePort    10.100.200.51   8080:30080/TCP   1m
```

---

## Access via NodePort

```powershell
# Get the minikube IP
minikube ip
# 192.168.49.2

# Access the app via NodePort (minikubeIP:30080)
curl http://$(minikube ip):30080/

# Or use minikube's built-in shortcut
minikube service hello-java-node
# This opens the app in your browser automatically
```

---

## Access via port-forward (ClusterIP)

```powershell
kubectl port-forward service/hello-java-cluster 8080:8080
# Now access: http://localhost:8080/
```

---

## Service DNS (How Pods Find Each Other)

Inside the cluster, every Service gets a DNS name:
```
<service-name>.<namespace>.svc.cluster.local
```

So from any pod in the `default` namespace:
```
curl http://hello-java-cluster:8080/health
curl http://hello-java-cluster.default.svc.cluster.local:8080/health
```

This is how the Java app will connect to PostgreSQL:
```yaml
DB_HOST: postgres-service    # K8s DNS resolves this to the postgres Service IP
```

---

## Clean Up

```powershell
kubectl delete -f clusterip-service.yaml
kubectl delete -f nodeport-service.yaml
kubectl delete -f ../03-deployments/deployment.yaml
```

---

## Next Step

→ [../05-configmaps-secrets/01-configmaps-secrets.md](../05-configmaps-secrets/01-configmaps-secrets.md) — Externalize config
