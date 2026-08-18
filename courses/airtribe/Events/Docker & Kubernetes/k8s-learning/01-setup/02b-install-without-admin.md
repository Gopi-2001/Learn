# Install kubectl & minikube — No Admin Access

Use either Scoop or a direct binary download. Both write only to your user profile.

---

## Option A — Scoop (Recommended)

Scoop installs everything into `~\scoop\` — no admin ever required.

```powershell
# Install Scoop
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression

# Install kubectl and minikube
scoop install kubectl
scoop install minikube

# Verify
kubectl version --client
minikube version
```

Update everything later with: `scoop update *`

---

## Option B — Manual Binary Download

```powershell
# Create a personal bin folder
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\bin"

# Download kubectl.exe
$k8sVersion = "v1.29.0"
Invoke-WebRequest -Uri "https://dl.k8s.io/release/$k8sVersion/bin/windows/amd64/kubectl.exe" `
  -OutFile "$env:USERPROFILE\bin\kubectl.exe"

# Download minikube.exe
Invoke-WebRequest -Uri "https://github.com/kubernetes/minikube/releases/latest/download/minikube-windows-amd64.exe" `
  -OutFile "$env:USERPROFILE\bin\minikube.exe"

# Add to USER PATH (writes to HKCU — no admin needed)
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
[Environment]::SetEnvironmentVariable("PATH", "$currentPath;$env:USERPROFILE\bin", "User")

# Reload PATH in the current terminal
$env:PATH += ";$env:USERPROFILE\bin"

# Verify
kubectl version --client
minikube version
```

---

## After Installation

Continue from [02-install-minikube.md](02-install-minikube.md) at **Step 3 — Start Your Cluster**.
