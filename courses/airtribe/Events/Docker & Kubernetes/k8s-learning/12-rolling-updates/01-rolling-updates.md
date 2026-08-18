# Module 12 — Rolling Updates & Rollbacks

## What is a Rolling Update?

When you deploy a new version, Kubernetes replaces pods **one at a time** (or in small batches), ensuring there's always some pods running. This gives **zero-downtime deployments**.

```
Before update:  [v1] [v1] [v1]

During update:  [v2] [v1] [v1]  ← v1 pod replaced by v2
                [v2] [v2] [v1]  ← another v1 replaced
                [v2] [v2] [v2]  ← update complete

If v2 is broken: kubectl rollout undo → back to [v1] [v1] [v1]
```

---

## Hands-On: Rolling Update

### Step 1 — Deploy v1

```powershell
# Make sure you have the v1 image loaded
minikube image load hello-java:1.0.0

kubectl apply -f ../03-deployments/deployment.yaml
kubectl apply -f ../04-services/nodeport-service.yaml

# Verify running
kubectl get pods
```

### Step 2 — Build and Load v2

To simulate a new version, modify HelloController.java:
```java
// Change "Hello from Docker!" to "Hello from Docker v2!"
```

Then build:
```powershell
cd c:\Learn\copilot\my-first-java-docker-app
docker build -t hello-java:2.0.0 .
minikube image load hello-java:2.0.0
```

### Step 3 — Update the Deployment

```powershell
# Method 1: Edit YAML, change image: hello-java:1.0.0 to hello-java:2.0.0
#           then: kubectl apply -f deployment.yaml

# Method 2: Imperative command
kubectl set image deployment/hello-java hello-java-container=hello-java:2.0.0

# Watch the rolling update happen in real time
kubectl rollout status deployment/hello-java
# Waiting for deployment "hello-java" rollout to finish...
# 1 out of 3 new replicas have been updated...
# 2 out of 3 new replicas have been updated...
# Deployment "hello-java" successfully rolled out

# Check that new pods are v2
kubectl describe pods | findstr "Image:"
```

### Step 4 — Rollback (if something is wrong)

```powershell
# View rollout history
kubectl rollout history deployment/hello-java
# REVISION  CHANGE-CAUSE
# 1         <none>
# 2         <none>

# Rollback to previous version
kubectl rollout undo deployment/hello-java

# Rollback to a specific revision
kubectl rollout undo deployment/hello-java --to-revision=1

# Watch rollback
kubectl rollout status deployment/hello-java
```

---

## Rolling Update Configuration (in deployment.yaml)

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 1    # at most 1 pod is unavailable during update
    maxSurge: 1          # at most 1 extra pod exists during update
```

## Other Strategies

**Recreate** — delete all old pods first, then create new ones (causes downtime):
```yaml
strategy:
  type: Recreate
```
Use for apps that can't run two versions simultaneously (e.g., DB schema migration).

---

## Add Change Cause to History

```powershell
# Add --record flag (deprecated) or use annotation:
kubectl annotate deployment/hello-java kubernetes.io/change-cause="Update to v2.0.0"

# Now history shows the cause
kubectl rollout history deployment/hello-java
# REVISION  CHANGE-CAUSE
# 1         Initial deployment
# 2         Update to v2.0.0
```

---

## Next Step

→ [../13-helm/01-helm-intro.md](../13-helm/01-helm-intro.md) — Package everything with Helm
