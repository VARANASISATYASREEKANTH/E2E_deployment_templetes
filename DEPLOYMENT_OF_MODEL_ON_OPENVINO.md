
# **Deploy AI/ML Models to OpenVINO**

## **Overview**
This guide explains how to convert and deploy AI/ML models to **Intel OpenVINO** for optimized inference on **Intel CPUs, GPUs, VPUs, and FPGAs**.

---

## **1. Convert AI/ML Model to OpenVINO IR Format**
### **Supported Frameworks**  
✅ TensorFlow (`.pb`, `.saved_model`)  
✅ ONNX (`.onnx`)  
✅ PyTorch (via ONNX conversion)  
✅ Caffe (`.caffemodel`)  

### **Step 1: Install OpenVINO**  
Install OpenVINO Toolkit:
```sh
pip install openvino-dev
```

### **Step 2: Convert Model to OpenVINO IR Format**
#### **Example 1: Convert TensorFlow Model**
```sh
mo --input_model model.pb --output_dir ./ir_model --input_shape [1,224,224,3]
```

#### **Example 2: Convert ONNX Model**
```sh
mo --input_model model.onnx --output_dir ./ir_model
```

#### **Example 3: Convert PyTorch Model (via ONNX)**
First, export PyTorch model to ONNX:
```python
import torch
import torchvision.models as models

model = models.resnet50(pretrained=True)
dummy_input = torch.randn(1, 3, 224, 224)
torch.onnx.export(model, dummy_input, "model.onnx")
```
Then, convert ONNX to OpenVINO IR:
```sh
mo --input_model model.onnx --output_dir ./ir_model
```

---

## **2. Deploy and Run Model in OpenVINO**

### **Step 1: Load Model with OpenVINO**
```python
from openvino.runtime import Core

ie = Core()
model = ie.read_model(model="ir_model/model.xml")
compiled_model = ie.compile_model(model=model, device_name="CPU")

input_layer = compiled_model.input(0)
output_layer = compiled_model.output(0)
```

### **Step 2: Run Inference**
```python
import numpy as np

# Example input
input_data = np.random.rand(1, 3, 224, 224).astype(np.float32)
output = compiled_model([input_data])

print(output)
```

---

## **3. Deploying OpenVINO Model on Edge Devices**
After conversion, deploy the model on:
✅ **Intel CPU** (Core, Xeon)  
✅ **Intel GPU** (iGPU)  
✅ **VPU** (Myriad X)  
✅ **FPGA**  

Use OpenVINO’s **Benchmark Tool** to test performance:
```sh
benchmark_app -m ir_model/model.xml -d CPU -niter 100
```

---

## **4. Alternative: Using NVIDIA TensorRT Instead of OpenVINO**
If deploying on **NVIDIA GPUs**, use **TensorRT** instead of OpenVINO:

1. Convert the model to **ONNX**
2. Optimize it using TensorRT:
```sh
trtexec --onnx=model.onnx --saveEngine=model.trt
```
3. Run inference on TensorRT engine.

---

## **Conclusion**
✅ **For Intel hardware**, use **OpenVINO** (convert model to IR `.xml, .bin`)  
✅ **For NVIDIA GPUs**, use **TensorRT** (convert to `.trt`)  

### 🚀 Deploy AI efficiently with OpenVINO! 🚀
