# Module 13 — Helm Charts

## What is Helm?

Helm is the **package manager for Kubernetes** — like npm for Node.js or Maven for Java.

```
npm package.json + dependencies  →  npm install
Maven pom.xml + dependencies     →  mvn package

Helm Chart (templates + values)  →  helm install
```

Without Helm, you maintain many separate YAML files and change values manually. With Helm, you have one **chart** with **templated** YAML, and you override values per environment.

---

## Helm Concepts

```
Chart         → package containing all K8s manifests as templates
Values        → default variables for the templates (values.yaml)
Release       → a specific installation of a chart
Repository    → a collection of published charts (like npm registry)
```

---

## Why Helm?

| Problem | Without Helm | With Helm |
|---|---|---|
| Deploy to dev/prod | Edit YAML files manually | `helm install --values prod.yaml` |
| Share your app | Send multiple YAML files | Publish one chart |
| Versioning | Git tags | `helm upgrade myapp ./chart --version 2.0.0` |
| Rollback | `kubectl rollout undo` | `helm rollback myapp 1` |
| Package dependencies | Manual | `helm dependency update` |

---

## Install Helm

```powershell
# Option A: winget
winget install -e --id Helm.Helm

# Option B: Chocolatey
choco install kubernetes-helm

# Verify
helm version
```

---

## Create Your First Chart

```powershell
# Create chart scaffold
helm create hello-java-chart
# Creates the directory structure automatically

# Examine what was created:
# hello-java-chart/
#   Chart.yaml          ← chart metadata
#   values.yaml         ← default values
#   templates/          ← K8s manifest templates
#     deployment.yaml
#     service.yaml
#     ingress.yaml
#     _helpers.tpl      ← reusable template functions
#     NOTES.txt         ← post-install instructions
```

We have already created a working chart in the [hello-java-chart/](hello-java-chart/) directory. Let's use it.

---

## Install the Chart

```powershell
# Dry run first — see what YAML would be generated
helm install hello-java ./hello-java-chart --dry-run --debug

# Install with default values
helm install hello-java ./hello-java-chart

# Check
helm list
kubectl get all

# Test with port-forward
kubectl port-forward svc/hello-java 8080:8080
curl http://localhost:8080/
```

---

## Override Values for Different Environments

```powershell
# Install for production with 5 replicas and different image tag
helm install hello-java-prod ./hello-java-chart \
  --set replicaCount=5 \
  --set image.tag=2.0.0 \
  --set ingress.enabled=true \
  --set ingress.host=hello-java.prod.com

# Use a values file (better for many overrides)
helm install hello-java-dev ./hello-java-chart -f values-dev.yaml
helm install hello-java-prod ./hello-java-chart -f values-prod.yaml
```

---

## Upgrade and Rollback

```powershell
# Upgrade to new image version
helm upgrade hello-java ./hello-java-chart --set image.tag=2.0.0

# See release history
helm history hello-java

# Rollback to revision 1
helm rollback hello-java 1
```

---

## Explore Public Charts

```powershell
# Add the Bitnami chart repository
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Search for PostgreSQL chart
helm search repo bitnami/postgresql

# Install PostgreSQL from Bitnami chart (instead of writing your own YAML!)
helm install my-postgres bitnami/postgresql \
  --set auth.username=appuser \
  --set auth.password=supersecret \
  --set auth.database=appdb
```

---

## Uninstall

```powershell
helm uninstall hello-java
```

---

## You've Completed the Learning Path! 🎉

```
Module 01: K8s concepts + minikube setup  ✅
Module 02: Pods                            ✅
Module 03: Deployments (self-healing)      ✅
Module 04: Services (networking)           ✅
Module 05: ConfigMaps + Secrets           ✅
Module 06: Namespaces                      ✅
Module 07: Persistent Storage + StatefulSet ✅
Module 08: Ingress                         ✅
Module 09: Full Stack (Java+PG+Redis)      ✅
Module 10: Horizontal Pod Autoscaler       ✅
Module 11: Resource Requests & Limits      ✅
Module 12: Rolling Updates & Rollbacks     ✅
Module 13: Helm Charts                     ✅
```

### What to Learn Next
- **RBAC** — role-based access control
- **Network Policies** — firewall rules between pods
- **Service Mesh** (Istio / Linkerd) — mTLS, traffic management
- **GitOps** (ArgoCD / Flux) — automated deployments from Git
- **Cloud K8s** — AKS (Azure), EKS (AWS), GKE (Google Cloud)
- **Operators** — extend K8s with custom controllers
