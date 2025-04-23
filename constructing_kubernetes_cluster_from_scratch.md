# 1. Kubernetes Cluster Setup from Scratch

![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)

A step-by-step guide to setting up a Kubernetes cluster from scratch using kubeadm on Ubuntu nodes.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Cluster Setup](#cluster-setup)
  - [1. Prepare All Nodes](#1-prepare-all-nodes)
  - [2. Install Container Runtime](#2-install-container-runtime)
  - [3. Install Kubernetes Tools](#3-install-kubernetes-tools)
  - [4. Initialize Master Node](#4-initialize-master-node)
  - [5. Install CNI Plugin](#5-install-cni-plugin)
  - [6. Join Worker Nodes](#6-join-worker-nodes)
- [Verification](#verification)
- [Testing the Cluster](#testing-the-cluster)
- [Optional Components](#optional-components)
- [Troubleshooting](#troubleshooting)
- [Going to Production](#going-to-production)

## Prerequisites
- At least two Ubuntu 20.04/22.04 machines:
  - 1 Master Node (2+ vCPUs, 2GB+ RAM)
  - 1+ Worker Nodes (1+ vCPUs, 1GB+ RAM)
- Unique hostnames on all nodes
- SSH access between nodes
- Open ports: `6443`, `2379-2380`, `10250-10252`, `30000-32767`

## Cluster Setup

### 1. Prepare All Nodes
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Disable swap
sudo swapoff -a
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

# Install dependencies
sudo apt install -y apt-transport-https ca-certificates curl

# Disable swap
sudo swapoff -a
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

# Install dependencies
sudo apt install -y apt-transport-https ca-certificates curl
```

### 2. Install Container Runtime
```
# Install Docker
sudo apt install -y docker.io
sudo systemctl enable --now docker

# Verify installation
sudo docker run hello-world
```

### 3. Install Kubernetes Tools
```
# Add Kubernetes repo
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo apt-add-repository "deb http://apt.kubernetes.io/ kubernetes-xenial main"

# Install components
sudo apt update
sudo apt install -y kubeadm kubelet kubectl
sudo apt-mark hold kubeadm kubelet kubectl
```
### 4. Initialize Master Node

``` # Initialize cluster
sudo kubeadm init --pod-network-cidr=10.244.0.0/16

# Configure kubectl
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Save join command (from output)
kubeadm join <MASTER_IP>:6443 --token <TOKEN> --discovery-token-ca-cert-hash <HASH>
```

### 5. Install CNI Plugin (Calico)
```
kubectl apply -f https://raw.githubusercontent.com/projectcalico/calico/v3.26.1/manifests/calico.yaml
```

### 6. Join Worker Nodes
On each worker node, run the join command from step 4:
```
sudo kubeadm join <MASTER_IP>:6443 --token <TOKEN> --discovery-token-ca-cert-hash <HASH>
```
### 7. Verification
``` kubectl get nodes
# Expected output (after few minutes):
# NAME     STATUS   ROLES           AGE   VERSION
# master   Ready    control-plane   5m    v1.27.x
# worker   Ready    <none>          2m    v1.27.x
```

### 8. Testing the Cluster
``` # Create test deployment
kubectl create deployment nginx --image=nginx

# Expose service
kubectl expose deployment nginx --port=80 --type=NodePort

# Get service details
kubectl get svc nginx
# Access using worker node IP and NodePort
curl http://<WORKER_IP>:<NODE_PORT>
```

### 9. Other miscellaneous
Kubernetes Dashboard
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```
### Firewall Rules (UFW)

```sudo ufw allow 6443/tcp
sudo ufw allow 2379/tcp
sudo ufw allow 10250/tcp
```
### Troubleshooting
Nodes Not Ready: Check CNI plugin installation

### Expired Join Token:

```
kubeadm token create --print-join-command
```
### View Cluster Events:
```
kubectl get events --all-namespaces
```
### Going to Production
Consider adding:

- High Availability (HA) control plane

- Persistent Volume provisioning

- Role-Based Access Control (RBAC)

- Cluster monitoring (Prometheus/Grafana)

- Ingress controller (Nginx, Traefik)

- Automated backups (Velero)


To use this:
1. Save as `README.md`
2. Commit to your Kubernetes cluster setup repository
3. Adjust versions/parameters as needed for your environment


