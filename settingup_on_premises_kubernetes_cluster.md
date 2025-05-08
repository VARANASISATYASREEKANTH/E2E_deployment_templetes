
# 🏗️ On-Premises Kubernetes Cluster Setup (Using kubeadm)

This guide walks you through setting up a basic on-premises Kubernetes cluster using `kubeadm`. It assumes you're working with Ubuntu or other Debian-based Linux distributions.

---

## 🧰 Prerequisites

- **Machines (physical or virtual):**
  - 1 Master node
  - 1 or more Worker nodes

- **Minimum specs per node:**
  - 2 CPUs
  - 2 GB RAM (4+ GB recommended for Master)
  - 20 GB disk

- **OS:** Ubuntu 20.04+ or CentOS 7/8

- **Networking:**
  - Nodes must communicate with each other over a private network.
  - Ensure ports like `6443`, `10250`, and others are not blocked.

---

## ⚙️ Setup Steps

### 1. Set Hostnames and Hosts File

```bash
sudo hostnamectl set-hostname <hostname>
# Example:
sudo hostnamectl set-hostname master-node
```

Edit `/etc/hosts` on **all nodes**:

```text
192.168.1.10 master-node
192.168.1.11 worker-node1
192.168.1.12 worker-node2
```

---

### 2. Disable Swap

```bash
sudo swapoff -a
sudo sed -i '/ swap / s/^/#/' /etc/fstab
```

---

### 3. Install Container Runtime (containerd)

```bash
sudo apt update
sudo apt install -y containerd
sudo mkdir -p /etc/containerd
containerd config default | sudo tee /etc/containerd/config.toml
sudo systemctl restart containerd
sudo systemctl enable containerd
```

---

### 4. Install Kubernetes Components

```bash
sudo apt-get update
sudo apt-get install -y apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" |   sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
```

---

### 5. Initialize the Control Plane (Master Node Only)

```bash
sudo kubeadm init --pod-network-cidr=192.168.0.0/16
```

After completion, run the following:

```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

---

### 6. Install Pod Network Add-on (Calico)

```bash
kubectl apply -f https://raw.githubusercontent.com/projectcalico/calico/v3.26.1/manifests/calico.yaml
```

---

### 7. Join Worker Nodes to the Cluster

Run the command printed at the end of the master node initialization on each worker node:

```bash
sudo kubeadm join <master-ip>:6443 --token <token>   --discovery-token-ca-cert-hash sha256:<hash>
```

> Example:
>
> ```bash
> sudo kubeadm join 192.168.1.10:6443 --token abcdef.0123456789abcdef >   --discovery-token-ca-cert-hash sha256:123456abcdef...
> ```

---

### 8. Verify Cluster Status

```bash
kubectl get nodes
kubectl get pods --all-namespaces
```

---

## ✅ Optional Configurations

### Kubernetes Dashboard

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```

Access with:

```bash
kubectl proxy
```

Then navigate to [http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/](http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/)

---

### Metrics Server

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

---

## 🔒 Tips

- Use **RBAC** for managing access controls.
- Consider **multi-master** for high availability.
- Automate with tools like **Ansible**, **Terraform**, or **kubespray**.

---

## 📘 Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [kubeadm Setup Guide](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/)
- [Calico Networking](https://docs.tigera.io/calico/latest/)

---

## 🙋 Need Help?

Feel free to open an issue or contribute to improve this guide!
