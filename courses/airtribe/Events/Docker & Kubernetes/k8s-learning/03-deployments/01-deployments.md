# Module 03 — Deployments

## What is a Deployment?

A **Deployment** tells Kubernetes:
- What container image to run
- How many replicas (copies) to keep running
- How to roll out updates

The Deployment creates a **ReplicaSet**, which creates and manages **Pods**.

```
Deployment  →  ReplicaSet  →  Pod  Pod  Pod
(you write)    (K8s creates)   (K8s creates)
```

---

## Deployment vs Raw Pod

| | Raw Pod | Deployment |
|---|---|---|
| Self-healing | ❌ Pod dies → gone forever | ✅ Pod dies → new one created |
| Scaling | ❌ Manual | ✅ `--replicas=5` |
| Rolling updates | ❌ Delete + recreate | ✅ One pod at a time |
| Rollback | ❌ Manual | ✅ `kubectl rollout undo` |

> **Always use Deployments in practice. Raw pods are only for learning.**

---

## Apply the Deployment

```powershell
kubectl apply -f deployment.yaml

# Watch pods start up
kubectl get pods -w
# NAME                          READY   STATUS    RESTARTS
# hello-java-5d98c-abc12        1/1     Running   0
# hello-java-5d98c-def34        1/1     Running   0
# hello-java-5d98c-ghi56        1/1     Running   0
```

---

## Hands-On: Self-Healing

```powershell
# Delete one pod — K8s recreates it immediately
kubectl delete pod <pod-name>
kubectl get pods -w    # watch a new pod appear

# Delete ALL pods — K8s recreates them all
kubectl delete pods --all
kubectl get pods -w    # watch them all come back
```

---

## Hands-On: Scaling

```powershell
# Scale up to 5 replicas
kubectl scale deployment hello-java --replicas=5
kubectl get pods

# Scale back down to 2
kubectl scale deployment hello-java --replicas=2
kubectl get pods
```

---

## Inspect

```powershell
kubectl get deployments
kubectl describe deployment hello-java

# See the ReplicaSet K8s created
kubectl get replicasets

# Rollout status
kubectl rollout status deployment/hello-java
```

---

## Hands-On: Rolling Update

```powershell
# Pretend you have a new version hello-java:2.0.0
# (Build it: docker build -t hello-java:2.0.0 . && minikube image load hello-java:2.0.0)

# Update the image — K8s replaces pods one at a time (no downtime)
kubectl set image deployment/hello-java hello-java-container=hello-java:2.0.0

# Watch the rolling update
kubectl rollout status deployment/hello-java

# Rollback if something is wrong
kubectl rollout undo deployment/hello-java

# See rollout history
kubectl rollout history deployment/hello-java
```

---

## Clean Up

```powershell
kubectl delete -f deployment.yaml
```

---

## Next Step

→ [../04-services/01-services.md](../04-services/01-services.md) — Expose your app with a Service
