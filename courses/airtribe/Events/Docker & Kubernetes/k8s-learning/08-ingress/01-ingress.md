# Module 08 — Ingress

## What is Ingress?

**Ingress** is an HTTP/HTTPS router that sits in front of your Services. It's like **Nginx** in your Docker Compose setup — but managed by Kubernetes.

```
Docker Compose:                  Kubernetes:
──────────────────               ────────────────────────────────────────
nginx (port 8080)                Ingress Controller (port 80/443)
  proxy_pass http://api:8080         /         → hello-java-service:8080
                                     /api/v2   → other-service:8080
                                     /admin    → admin-service:3000
```

Without Ingress, you'd need a NodePort for each service. Ingress multiplexes many services through a single IP.

---

## Step 1 — Enable Ingress in Minikube

```powershell
# Enable the Nginx Ingress addon
minikube addons enable ingress

# Verify the ingress controller pod is running
kubectl get pods -n ingress-nginx
# NAME                                        READY   STATUS    RESTARTS
# ingress-nginx-controller-7799c6795f-xxx     1/1     Running   0
```

---

## Step 2 — Apply Deployment + Service + Ingress

```powershell
# Deploy the app
kubectl apply -f ../03-deployments/deployment.yaml

# ClusterIP service (Ingress talks to ClusterIP, not NodePort)
kubectl apply -f ../04-services/clusterip-service.yaml

# Apply the Ingress rule
kubectl apply -f ingress.yaml

# Check the Ingress
kubectl get ingress
# NAME              CLASS   HOSTS               ADDRESS          PORTS   AGE
# hello-java-ingress nginx  hello-java.local    192.168.49.2     80      1m
```

---

## Step 3 — Add to Hosts File

The Ingress uses the hostname `hello-java.local`. Add it to your hosts file:

```powershell
# Get minikube IP
minikube ip
# 192.168.49.2

# Run PowerShell as Administrator and add:
Add-Content -Path "C:\Windows\System32\drivers\etc\hosts" -Value "192.168.49.2 hello-java.local"

# Test
curl http://hello-java.local/
curl http://hello-java.local/health
```

---

## Path-Based Routing (bonus)

Ingress can route different paths to different services:

```yaml
rules:
  - host: myapp.local
    http:
      paths:
        - path: /api
          backend: api-service:8080
        - path: /admin
          backend: admin-service:3000
```

---

## Clean Up

```powershell
kubectl delete -f ingress.yaml
kubectl delete -f ../04-services/clusterip-service.yaml
kubectl delete -f ../03-deployments/deployment.yaml
```

---

## Next Step

→ [../09-full-stack/01-full-stack-guide.md](../09-full-stack/01-full-stack-guide.md) — Deploy the full Java + PostgreSQL + Redis stack on Kubernetes
