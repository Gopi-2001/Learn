# Module 01 — kubectl Basics

## kubectl = Your K8s Command Line

`kubectl` talks to the Kubernetes API Server. Everything you do — creating Pods, reading logs, scaling apps — goes through kubectl.

```
You  →  kubectl  →  K8s API Server  →  etcd / Scheduler / Nodes
```

---

## Command Structure

```
kubectl  <verb>  <resource-type>  <resource-name>  [flags]
         ──────  ──────────────  ─────────────────  ───────
         get     pod             hello-pod          -n myapp
         apply   deployment      api-deployment     --dry-run=client
         delete  service         api-service        -o yaml
         logs    pod             api-pod-abc123     -f
         exec    pod             api-pod-abc123     -- sh
```

---

## Essential Commands

### Get / List resources

```powershell
# List all pods in current namespace (default)
kubectl get pods

# List pods in all namespaces
kubectl get pods -A

# List pods with more details (node, IP)
kubectl get pods -o wide

# List everything at once
kubectl get all

# List pods, deployments and services together
kubectl get pods,deployments,services

# Watch live (like docker stats)
kubectl get pods -w
```

### Describe (detailed info — events, conditions)

```powershell
# Describe a pod — very useful for debugging
kubectl describe pod <pod-name>

# Describe a deployment
kubectl describe deployment <deployment-name>
```

### Logs (like docker logs)

```powershell
kubectl logs <pod-name>
kubectl logs <pod-name> -f                    # follow (live tail)
kubectl logs <pod-name> --previous            # logs from crashed container
kubectl logs <pod-name> -c <container-name>   # multi-container pod
```

### Exec (like docker exec)

```powershell
# Open a shell in a running pod
kubectl exec -it <pod-name> -- sh

# Run a single command
kubectl exec <pod-name> -- env
kubectl exec <pod-name> -- cat /app/application.properties
```

### Apply / Create

```powershell
# Apply a manifest (create or update)
kubectl apply -f pod.yaml
kubectl apply -f ./k8s/          # apply all YAML files in a folder
kubectl apply -f https://...     # apply from URL

# Dry run — see what would happen without actually creating
kubectl apply -f deployment.yaml --dry-run=client
```

### Delete

```powershell
kubectl delete pod <pod-name>
kubectl delete -f pod.yaml        # delete what the file describes
kubectl delete pod --all          # delete all pods in current namespace
```

### Scale

```powershell
kubectl scale deployment <name> --replicas=3
```

### Port Forward (like docker -p, but temporary)

```powershell
# Forward localhost:8080 → pod port 8080
kubectl port-forward pod/<pod-name> 8080:8080

# Forward to a service
kubectl port-forward service/<service-name> 8080:8080
```

### Namespace

```powershell
# Set default namespace for all commands (so you don't type -n myapp every time)
kubectl config set-context --current --namespace=myapp

# View current context
kubectl config current-context
kubectl config get-contexts
```

### Rollout (for Deployments)

```powershell
kubectl rollout status deployment/<name>
kubectl rollout history deployment/<name>
kubectl rollout undo deployment/<name>      # rollback
```

---

## Output Formats

```powershell
kubectl get pod <name> -o yaml    # full YAML spec — great for learning
kubectl get pod <name> -o json    # JSON format
kubectl get pod <name> -o wide    # extra columns
```

---

## Quick Debug Workflow

When something isn't working:

```powershell
# 1. Check Pod status
kubectl get pods

# 2. If status is not "Running", describe it
kubectl describe pod <pod-name>
# Look at the "Events" section at the bottom

# 3. Check logs
kubectl logs <pod-name>

# 4. Shell in (if the pod is Running)
kubectl exec -it <pod-name> -- sh

# 5. Check the deployment
kubectl describe deployment <name>
```

---

## Your Most Used Commands (Cheat Sheet)

```powershell
kubectl get pods                              # list pods
kubectl get all                               # list everything
kubectl apply -f <file>.yaml                  # create/update
kubectl delete -f <file>.yaml                 # delete
kubectl logs -f <pod-name>                    # follow logs
kubectl exec -it <pod-name> -- sh             # shell into pod
kubectl describe pod <pod-name>               # debug events
kubectl port-forward svc/<name> 8080:8080     # access service locally
kubectl get pods -w                           # watch pods change
```

---

## Next Step

→ [../02-pods/01-pods.md](../02-pods/01-pods.md) — Deploy your first Pod
