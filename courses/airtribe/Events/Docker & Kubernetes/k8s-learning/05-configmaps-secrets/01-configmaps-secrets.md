# Module 05 — ConfigMaps & Secrets

## The Problem: Hardcoded Config

In Docker Compose you used `.env` files and `environment:` keys. In Kubernetes:

```
.env file           →  ConfigMap  (non-sensitive config)
                    →  Secret     (passwords, API keys, tokens)
```

**Never hardcode config in your YAML or Docker image.**

---

## ConfigMap — Non-Sensitive Config

ConfigMap stores plain text key-value pairs:
- Database host, port, app name, log level
- Spring profile, feature flags

```yaml
# In Docker Compose:
environment:
  DB_HOST: db
  DB_PORT: "5432"

# In Kubernetes (ConfigMap):
data:
  DB_HOST: "postgres-service"
  DB_PORT: "5432"
```

---

## Secret — Sensitive Config

Secret stores **base64-encoded** values (not encrypted by default, but treated differently by K8s):
- Passwords, API keys, TLS certificates, tokens

> ⚠️ Base64 is encoding, NOT encryption. Anyone with `kubectl get secret` can decode it.
> For production, use external secret managers (AWS Secrets Manager, HashiCorp Vault, etc.)

---

## How to Create a Secret Value

```powershell
# Encode a password for the YAML file
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("supersecret"))
# Result: c3VwZXJzZWNyZXQ=
```

---

## Apply and Test

```powershell
# Apply ConfigMap and Secret
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml

# Apply Deployment that reads from them
kubectl apply -f deployment-with-config.yaml

# Verify pods are running
kubectl get pods

# Verify env vars are injected
kubectl exec -it <pod-name> -- env | findstr DB
# DB_HOST=postgres-service
# DB_PORT=5432
# DB_NAME=appdb

# Read the secret (base64 encoded)
kubectl get secret java-app-secret -o yaml

# Decode the password
kubectl get secret java-app-secret -o jsonpath='{.data.DB_PASSWORD}' | powershell -Command "[System.Convert]::FromBase64String([System.IO.StreamReader]::new([System.Console]::OpenStandardInput()).ReadToEnd())"
```

---

## Two Ways to Inject ConfigMap/Secret into a Pod

**Method 1: envFrom (inject ALL keys as env vars)**
```yaml
envFrom:
  - configMapRef:
      name: java-app-config
  - secretRef:
      name: java-app-secret
```

**Method 2: env with valueFrom (inject specific keys)**
```yaml
env:
  - name: DB_HOST
    valueFrom:
      configMapKeyRef:
        name: java-app-config
        key: DB_HOST
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: java-app-secret
        key: DB_PASSWORD
```

The deployment-with-config.yaml uses Method 2 for clarity.

---

## ConfigMap as Files (bonus)

ConfigMaps can also be mounted as files (useful for application.properties, nginx.conf, etc.):

```yaml
volumes:
  - name: config-volume
    configMap:
      name: java-app-config
volumeMounts:
  - name: config-volume
    mountPath: /app/config
```

---

## Clean Up

```powershell
kubectl delete -f deployment-with-config.yaml
kubectl delete -f configmap.yaml
kubectl delete -f secret.yaml
```

---

## Next Step

→ [../06-namespaces/01-namespaces.md](../06-namespaces/01-namespaces.md) — Organize resources
