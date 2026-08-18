# Module 02 — Pods

## What is a Pod?

A **Pod** is the **smallest deployable unit** in Kubernetes. It wraps one or more containers.

```
Docker:       container = the unit
Kubernetes:   Pod = the unit (usually contains 1 container)
```

Think of a Pod as a "container wrapper" that adds:
- A shared network namespace (containers in the same pod share an IP)
- A shared storage (volumes)
- Lifecycle management

> **Rule of thumb**: 1 Pod = 1 container (except sidecar patterns)

---

## Prepare: Build the Java Image

```powershell
# From my-first-java-docker-app directory:
cd c:\Learn\copilot\my-first-java-docker-app
docker build -t hello-java:1.0.0 .

# Load into minikube so K8s can use it
minikube image load hello-java:1.0.0

# Verify the image is in minikube
minikube image ls | findstr hello-java
```

---

## Your First Pod

Look at [pod.yaml](pod.yaml) — this is the minimal pod definition:

```
docker run -p 8080:8080 hello-java:1.0.0
    ≈
kubectl apply -f pod.yaml
```

Apply it:

```powershell
kubectl apply -f pod.yaml

# Watch it start
kubectl get pods -w
# NAME         READY   STATUS    RESTARTS   AGE
# hello-pod    1/1     Running   0          30s
```

---

## Access the App

A raw Pod has no stable external IP. Use port-forward for testing:

```powershell
kubectl port-forward pod/hello-pod 8080:8080
# Now open: http://localhost:8080/
# Response: {"message":"Hello from Docker! 🐳 (Java Edition)","status":"running"}
```

---

## Inspect the Pod

```powershell
# Basic info
kubectl get pod hello-pod
kubectl get pod hello-pod -o wide    # see which node it's on
kubectl get pod hello-pod -o yaml    # full spec K8s is using

# Detailed info + events (use this when debugging!)
kubectl describe pod hello-pod

# Logs (Spring Boot startup output)
kubectl logs hello-pod
kubectl logs -f hello-pod            # follow

# Shell inside the container
kubectl exec -it hello-pod -- sh
# Inside: ls /app           → app.jar
#         java -version     → JRE 21
#         wget -qO- http://localhost:8080/health

# Exit the shell
exit
```

---

## Pod Lifecycle

```
Pending   → K8s found a node, pulling image
Running   → all containers started
Succeeded → all containers exited with 0 (batch jobs)
Failed    → container exited with non-zero
Unknown   → node unreachable
```

---

## Why NOT to Use Raw Pods in Production

```
Problem 1: Pods don't self-heal
  → If hello-pod crashes, K8s does NOT restart it automatically
  → (Docker Compose has "restart: unless-stopped"; K8s raw pods don't)

Problem 2: Pods have no stable IP
  → Each new pod gets a different IP

Problem 3: No rolling updates
  → You can't update a pod in-place; must delete and recreate

Solution: Use a Deployment (Module 03) — it manages Pods for you
```

Demo — delete the pod and see it's gone forever:
```powershell
kubectl delete pod hello-pod
kubectl get pods
# No resources found — it's gone and won't come back!
```

---

## Clean Up

```powershell
kubectl delete -f pod.yaml
```

---

## Next Step

→ [../03-deployments/01-deployments.md](../03-deployments/01-deployments.md) — Self-healing Deployments
