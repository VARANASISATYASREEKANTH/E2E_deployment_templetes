
# 🚦 Dynamic Traffic Management in Kubernetes using Istio

This guide demonstrates how to dynamically manage traffic between service versions in Kubernetes using **Istio**. We use a canary deployment strategy to route traffic between two versions of a service.

---

## 🧩 Components Used

| Use Case | Tool |
|----------|------|
| Routing | Istio VirtualService |
| Subsets | Istio DestinationRule |
| Load Balancing | Kubernetes Service |
| Autoscaling | HPA (Horizontal Pod Autoscaler) |

---

## 1️⃣ Deploy Two Versions of the Application

### `deployment-v1.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-v1
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-app
      version: v1
  template:
    metadata:
      labels:
        app: my-app
        version: v1
    spec:
      containers:
      - name: my-app
        image: myregistry/my-app:v1
        ports:
        - containerPort: 80
```

### `deployment-v2.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-v2
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-app
      version: v2
  template:
    metadata:
      labels:
        app: my-app
        version: v2
    spec:
      containers:
      - name: my-app
        image: myregistry/my-app:v2
        ports:
        - containerPort: 80
```

---

## 2️⃣ Create a Kubernetes Service

### `service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  selector:
    app: my-app
  ports:
  - name: http
    port: 80
    targetPort: 80
```

---

## 3️⃣ Define Istio Traffic Routing

### `destination-rule.yaml`

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: my-app
spec:
  host: my-app-service
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
```

### `virtual-service.yaml`

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: my-app
spec:
  hosts:
  - "*"
  gateways:
  - my-gateway
  http:
  - route:
    - destination:
        host: my-app-service
        subset: v1
      weight: 80
    - destination:
        host: my-app-service
        subset: v2
      weight: 20
```

---

## 4️⃣ Optional: Enable Autoscaling

### `autoscaler.yaml`

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: my-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: my-app-v1
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 60
```

---

## ✅ Result

With this setup:
- 80% of traffic is routed to `v1`, 20% to `v2`
- You can dynamically shift traffic by editing the weights in `VirtualService`
- Traffic routing is managed without downtime
- Autoscaling helps manage load in real-time

---

## 📌 Notes

- Ensure Istio is installed in your cluster.
- You need a configured `Gateway` resource (`my-gateway`) to expose the app externally.
- For traffic mirroring, fault injection, or retries, expand the `VirtualService` config.

---
