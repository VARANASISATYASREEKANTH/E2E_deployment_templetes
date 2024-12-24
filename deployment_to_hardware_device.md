# ML Model Deployment with Kubernetes, Jenkins, and FPGA

This repository contains an end-to-end setup for deploying a trained machine learning model using Kubernetes, Jenkins, and FPGA hardware. The deployment includes a simple Flask application that serves predictions from the trained model, with additional instructions for deploying to FPGA devices.

## Repository Structure
```
my-ml-model-deployment/
│
├── model/                   # Directory for trained model files
│   └── model.pkl
│
├── app/                     # Flask/Django FastAPI app for serving the model
│   ├── Dockerfile           # Dockerfile for the app
│   ├── requirements.txt     # Python dependencies
│   ├── app.py               # Python script to serve the model (e.g., Flask API)
│
├── k8s/                     # Kubernetes manifests
│   ├── deployment.yaml      # Deployment configuration for the app
│   ├── service.yaml         # Service to expose the deployment
│   └── ingress.yaml         # Ingress for external access (optional)
│
├── fpga/                    # FPGA deployment scripts and configurations
│   ├── deploy_fpga.sh       # Script to deploy model to FPGA
│   ├── model_conversion.py  # Script for model conversion to FPGA-compatible format
│   └── README.md            # FPGA-specific instructions
│
├── jenkins/                 # Jenkins pipeline files
│   └── Jenkinsfile          # Jenkins pipeline script
│
└── README.md                # Instructions for setup and deployment
```

---

## Setup Instructions

### Prerequisites

1. Docker installed locally.
2. Kubernetes cluster set up (e.g., Minikube, GKE, AKS, EKS).
3. FPGA hardware and compatible drivers installed.
4. Jenkins installed and configured with:
   - Docker integration.
   - Kubernetes CLI installed.
   - Credentials for Docker Hub or any container registry.

---

### Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/my-ml-model-deployment.git
```

#### 2. Build and Push the Docker Image
```bash
cd my-ml-model-deployment/app

docker build -t your-dockerhub-username/ml-model:latest .
docker push your-dockerhub-username/ml-model:latest
```

#### 3. Deploy to Kubernetes
```bash
kubectl apply -f ../k8s/
```

#### 4. Deploy to FPGA

- Convert the model to an FPGA-compatible format:
```bash
cd ../fpga
python model_conversion.py --input ../model/model.pkl --output fpga_model.xmodel
```

- Use the deployment script to upload the model to the FPGA device:
```bash
bash deploy_fpga.sh fpga_model.xmodel
```

- Verify the deployment:
  - Check hardware logs for errors.
  - Ensure the model is running on the FPGA by querying the device's status.

#### 5. Set Up Jenkins

- Create a pipeline job in Jenkins.
- Use the `jenkins/Jenkinsfile` from this repository.
- Add Docker and Kubernetes credentials to Jenkins.

#### 6. Access the API

- Retrieve the LoadBalancer IP:
```bash
kubectl get svc ml-model-service
```

- Test the API endpoint:
```bash
curl -X POST http://<LoadBalancer-IP>/predict -H "Content-Type: application/json" -d '{"features": [1, 2, 3]}'
```

---

## File Descriptions

### `app/app.py`
A simple Flask app to serve predictions:
```python
from flask import Flask, request, jsonify
import pickle

app = Flask(__name__)

# Load the model
with open('model/model.pkl', 'rb') as f:
    model = pickle.load(f)

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    prediction = model.predict([data['features']])
    return jsonify({'prediction': prediction.tolist()})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

### `app/Dockerfile`
Dockerfile to containerize the Flask app:
```Dockerfile
# Use a lightweight Python image
FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Copy files
COPY . /app

# Install dependencies
RUN pip install -r requirements.txt

# Expose the port
EXPOSE 5000

# Run the app
CMD ["python", "app.py"]
```

### `k8s/deployment.yaml`
Kubernetes Deployment manifest:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ml-model-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ml-model
  template:
    metadata:
      labels:
        app: ml-model
    spec:
      containers:
      - name: ml-model-container
        image: your-dockerhub-username/ml-model:latest
        ports:
        - containerPort: 5000
```

### `k8s/service.yaml`
Kubernetes Service to expose the deployment:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: ml-model-service
spec:
  selector:
    app: ml-model
  ports:
    - protocol: TCP
      port: 80
      targetPort: 5000
  type: LoadBalancer
```

### `fpga/deploy_fpga.sh`
Script to deploy the model to FPGA:
```bash
#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <model.xmodel>"
    exit 1
fi

MODEL_FILE=$1
FPGA_DEVICE="your-fpga-device-identifier"

# Upload model to FPGA
echo "Uploading $MODEL_FILE to FPGA device $FPGA_DEVICE"
sudo fpga_upload_tool --device $FPGA_DEVICE --file $MODEL_FILE

if [ $? -eq 0 ]; then
    echo "Model successfully deployed to FPGA!"
else
    echo "Failed to deploy model to FPGA."
    exit 1
fi
```

### `jenkins/Jenkinsfile`
Jenkins pipeline script:
```groovy
pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git 'https://github.com/your-repo/my-ml-model-deployment.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t your-dockerhub-username/ml-model:latest app/'
            }
        }

        stage('Push Docker Image') {
            steps {
                withDockerRegistry([credentialsId: 'dockerhub-credentials', url: '']) {
                    sh 'docker push your-dockerhub-username/ml-model:latest'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/'
            }
        }

        stage('Deploy to FPGA') {
            steps {
                sh 'bash fpga/deploy_fpga.sh fpga/fpga_model.xmodel'
            }
        }
    }
}
```

---

## Additional Notes

- **Open Source License**: You may use OpenJDK for licensing purposes.
- **Customizations**: Update the `deployment.yaml` and `service.yaml` files as per your Kubernetes cluster setup.
- **Environment Variables**: Manage sensitive information using environment variables or Kubernetes Secrets.
- **FPGA Requirements**: Ensure your FPGA device and drivers are correctly installed and configured before deployment.

For any issues, feel free to raise an issue in this repository!

