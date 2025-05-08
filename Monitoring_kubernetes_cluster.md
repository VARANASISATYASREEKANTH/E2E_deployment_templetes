
# 📊 Monitoring Load and Traffic for Kubernetes Cluster Nodes

This guide explains how to monitor **CPU, memory, disk, and network traffic** across your Kubernetes cluster nodes.

---

## 🔍 1. `kubectl top` (via Metrics Server)

Install the **metrics-server**:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

Usage:

```bash
kubectl top nodes       # View node-level resource usage
kubectl top pods        # View pod-level resource usage
```

---

## 📈 2. Prometheus + Grafana (Full Monitoring Stack)

### Deploy with `kube-prometheus-stack`:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install kube-prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring --create-namespace
```

### Access Grafana:

```bash
kubectl port-forward svc/kube-prometheus-grafana 3000:80 -n monitoring
```

- Open: [http://localhost:3000](http://localhost:3000)
- Default credentials: `admin / prom-operator`

Grafana provides dashboards for:
- Node load (CPU, memory, disk)
- Pod metrics
- Network traffic
- Kubernetes API health

---

## 🛠️ 3. Node Tools (`htop`, `iftop`, `nload`, etc.)

SSH into each node:

```bash
htop       # Live system load
iftop      # Live network usage by interface
nload      # Real-time bandwidth usage
iostat     # Disk I/O stats
```

> Install with:
> ```bash
> sudo apt install htop iftop nload sysstat
> ```

---

## 🌐 4. Netdata (Per-node Monitoring GUI)

Install [Netdata](https://learn.netdata.cloud/docs/agent/packaging/installer):

```bash
bash <(curl -Ss https://my-netdata.io/kickstart.sh)
```

Access via `http://<node-ip>:19999/` for:
- CPU, memory, disk
- Network interface metrics
- Containers and processes

---

## 📦 5. Cilium + Hubble (Advanced Network Visibility)

If using **Cilium** as your CNI:

```bash
cilium hubble enable
cilium hubble ui
```

Gives:
- Real-time network flow visibility
- Service-to-service traffic map
- Network policy observability

---

## ✅ Summary Table

| Tool                     | Monitors       | Cluster-wide | Real-Time | Easy to Setup |
|--------------------------|----------------|---------------|------------|----------------|
| `kubectl top` + Metrics Server | CPU, memory    | ✅            | ❌           | ✅             |
| Prometheus + Grafana     | Everything     | ✅            | ✅           | ⚠️ (medium)     |
| `htop`, `iftop`, `nload` | Per node stats | ❌            | ✅           | ✅             |
| Netdata                  | Per node (GUI) | ❌            | ✅           | ✅             |
| Cilium + Hubble          | Network flows  | ✅            | ✅           | ⚠️ (advanced)   |

---

## 🧠 Recommendations

- Start with **Metrics Server** and **`kubectl top`** for basic metrics.
- Use **Prometheus + Grafana** for production-level observability.
- Add **Netdata** or **Cilium** for deeper traffic insights if needed.

---

## 📘 Resources

- [Metrics Server](https://github.com/kubernetes-sigs/metrics-server)
- [kube-prometheus-stack Helm Chart](https://github.com/prometheus-community/helm-charts)
- [Netdata Docs](https://learn.netdata.cloud/)
- [Cilium + Hubble](https://docs.cilium.io/en/stable/gettingstarted/hubble/)

---

## 🙋 Need Help?

Feel free to open an issue or contribute to improve this guide!
