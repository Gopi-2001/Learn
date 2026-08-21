# Module 07 — Persistent Storage (PV & PVC)

## The Problem: Containers Are Stateless

When a Pod restarts, its filesystem is wiped. For databases, this means **data loss**.

```
Docker Compose solution:
  volumes:
    - db-data:/var/lib/postgresql/data    ← named volume survives restarts

Kubernetes solution:
  PersistentVolume (PV)    →  the actual disk
  PersistentVolumeClaim (PVC)  →  a "request" for storage (your pod asks for PV)
```

---

## The Three Objects

```
StorageClass  →  defines HOW storage is provisioned (SSD, HDD, cloud disk)
     ↓
PersistentVolume (PV)  →  actual storage (1GB on disk, S3 bucket, etc.)
     ↓
PersistentVolumeClaim (PVC)  →  your pod's request for storage
     ↓
Pod (mounts the PVC as a volume)
```

In **minikube**, there's a built-in `standard` StorageClass that automatically provisions local disk storage — you don't need to create a PV manually.

---

## Hands-On: Deploy PostgreSQL with Persistent Storage

```powershell
# Apply PVC (dynamic provisioning — minikube creates the PV automatically)
kubectl apply -f pvc.yaml
kubectl get pvc
# NAME        STATUS   VOLUME                                     CAPACITY   ACCESS MODES
# postgres-pvc Bound   pvc-abc123...                              1Gi        RWO

# Apply PostgreSQL as a StatefulSet with the PVC
kubectl apply -f postgres-statefulset.yaml
kubectl get pods
# NAME         READY   STATUS    RESTARTS
# postgres-0   1/1     Running   0

# Test data persistence
kubectl exec -it postgres-0 -- psql -U appuser -d appdb
# Inside psql:
# CREATE TABLE test (id SERIAL, name TEXT);
# INSERT INTO test VALUES (1, 'hello from k8s');
# SELECT * FROM test;
# \q

# Delete the pod — StatefulSet recreates it
kubectl delete pod postgres-0
kubectl get pods -w    # watch postgres-0 restart

# Shell back in — data is still there!
kubectl exec -it postgres-0 -- psql -U appuser -d appdb
# SELECT * FROM test;   ← still shows "hello from k8s"
```

---

## StatefulSet vs Deployment

| | Deployment | StatefulSet |
|---|---|---|
| Pod names | random (api-5d98-abc12) | ordered (postgres-0, postgres-1) |
| Storage | shared or none | each pod gets its own PVC |
| Use for | stateless apps (API, web) | stateful apps (DB, Kafka, Zookeeper) |
| Restart order | parallel | ordered (0 first, then 1, then 2) |

---

## Access Modes

| Mode | Meaning | Common Use |
|---|---|---|
| `ReadWriteOnce (RWO)` | mounted by one node at a time | databases |
| `ReadOnlyMany (ROX)` | read by many nodes | config files |
| `ReadWriteMany (RWX)` | read/write by many nodes | shared filesystems |

---

## Clean Up

```powershell
kubectl delete -f postgres-statefulset.yaml
kubectl delete -f pvc.yaml
# Note: the PV is deleted automatically when PVC is deleted (dynamic provisioning)
```

---

## Next Step

→ [../08-ingress/01-ingress.md](../08-ingress/01-ingress.md) — Route HTTP traffic with Ingress
