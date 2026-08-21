# Module 06 — Namespaces

## What is a Namespace?

A **Namespace** is a logical partition inside a Kubernetes cluster. Think of it as a folder for your K8s resources.

```
Cluster
├── Namespace: default       ← your resources go here when you don't specify
├── Namespace: kube-system   ← K8s internal components (do not touch)
├── Namespace: myapp-dev     ← dev environment
├── Namespace: myapp-prod    ← production environment
└── Namespace: monitoring    ← Prometheus, Grafana
```

---

## Why Use Namespaces?

1. **Isolation**: dev team's resources don't conflict with prod
2. **RBAC**: give different teams different permissions per namespace
3. **Resource quotas**: limit how much CPU/memory a namespace can use
4. **Organization**: logical grouping of related services

---

## Hands-On

```powershell
# List existing namespaces
kubectl get namespaces
# NAME              STATUS   AGE
# default           Active   5d
# kube-system       Active   5d
# kube-public       Active   5d

# Create the namespace
kubectl apply -f namespace.yaml

# Deploy into the namespace
kubectl apply -f ../03-deployments/deployment.yaml -n myapp
kubectl apply -f ../04-services/nodeport-service.yaml -n myapp

# List pods in the myapp namespace
kubectl get pods -n myapp
kubectl get all -n myapp

# Set myapp as default (so you don't have to type -n myapp every time)
kubectl config set-context --current --namespace=myapp

# Access the service
minikube service hello-java-node -n myapp

# Switch back to default namespace
kubectl config set-context --current --namespace=default

# Delete the entire namespace (deletes ALL resources inside it)
kubectl delete namespace myapp
```

---

## Namespaces and DNS

Services in different namespaces use different DNS names:

```
Same namespace:         curl http://hello-java-cluster:8080/
Different namespace:    curl http://hello-java-cluster.myapp.svc.cluster.local:8080/
```

---

## Next Step

→ [../07-persistent-storage/01-persistent-storage.md](../07-persistent-storage/01-persistent-storage.md) — Persistent storage for databases
