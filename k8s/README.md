# URL Shortener - Kubernetes Deployment

## 📋 **Prerequisites**

- **Kubernetes cluster** (v1.20+)
- **kubectl** configured
- **Docker image** built and available
- **Ingress Controller** (nginx recommended)

## 🚀 **Quick Start**

### **1. Build Docker Image**
```bash
# Build image
docker build -t url-shortener:latest .

# Tag for registry (optional)
docker tag url-shortener:latest your-registry/url-shortener:latest
docker push your-registry/url-shortener:latest
```

### **2. Deploy to Kubernetes**
```bash
# Deploy all resources
kubectl apply -k k8s/

# Or deploy individually
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

### **3. Verify Deployment**
```bash
# Check pods
kubectl get pods -n url-shortener

# Check services
kubectl get svc -n url-shortener

# Check ingress
kubectl get ingress -n url-shortener

# Check HPA
kubectl get hpa -n url-shortener
```

## 🔧 **Configuration**

### **ConfigMap Variables**
- `SPRING_PROFILES_ACTIVE`: Spring profile (k8s)
- `BASE_URL`: Base URL for short URLs
- `SERVER_PORT`: Application port (8080)
- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`: Actuator endpoints
- `LOGGING_LEVEL_*`: Logging configuration

### **Resource Limits**
- **CPU**: 250m request, 500m limit
- **Memory**: 512Mi request, 1Gi limit
- **Replicas**: 3 (min: 2, max: 10 with HPA)

## 🌐 **Access**

### **NodePort (Direct Access)**
```bash
# Get node IP
kubectl get nodes -o wide

# Access via NodePort
curl http://<NODE_IP>:30080/
```

### **Ingress (Recommended)**
```bash
# Add to /etc/hosts
echo "127.0.0.1 url-shortener.local" | sudo tee -a /etc/hosts

# Access via ingress
curl http://url-shortener.local/
```

## 📊 **Monitoring**

### **Health Checks**
```bash
# Liveness probe
kubectl exec -n url-shortener deployment/url-shortener-app -- curl http://localhost:8080/actuator/health

# Readiness probe
kubectl exec -n url-shortener deployment/url-shortener-app -- curl http://localhost:8080/actuator/health
```

### **Logs**
```bash
# View logs
kubectl logs -n url-shortener deployment/url-shortener-app -f

# View logs from specific pod
kubectl logs -n url-shortener <POD_NAME> -f
```

## 🔄 **Scaling**

### **Manual Scaling**
```bash
# Scale deployment
kubectl scale deployment url-shortener-app -n url-shortener --replicas=5
```

### **Automatic Scaling (HPA)**
- **CPU**: 70% threshold
- **Memory**: 80% threshold
- **Min replicas**: 2
- **Max replicas**: 10

## 🛠 **Troubleshooting**

### **Common Issues**

1. **ImagePullBackOff**
   ```bash
   # Check image availability
   kubectl describe pod <POD_NAME> -n url-shortener
   ```

2. **CrashLoopBackOff**
   ```bash
   # Check logs
   kubectl logs <POD_NAME> -n url-shortener
   ```

3. **Service Not Accessible**
   ```bash
   # Check service endpoints
   kubectl get endpoints -n url-shortener
   ```

### **Debug Commands**
```bash
# Describe resources
kubectl describe deployment url-shortener-app -n url-shortener
kubectl describe service url-shortener-service -n url-shortener
kubectl describe ingress url-shortener-ingress -n url-shortener

# Port forward for testing
kubectl port-forward -n url-shortener svc/url-shortener-service 8080:8080
```

## 🧹 **Cleanup**

```bash
# Remove all resources
kubectl delete -k k8s/

# Or remove individually
kubectl delete -f k8s/hpa.yaml
kubectl delete -f k8s/ingress.yaml
kubectl delete -f k8s/service.yaml
kubectl delete -f k8s/deployment.yaml
kubectl delete -f k8s/configmap.yaml
kubectl delete -f k8s/namespace.yaml
```

## 📝 **Notes**

- **In-memory cache**: Data will be lost on pod restart
- **No persistence**: URLs are not persisted between restarts
- **Production ready**: Includes health checks, resource limits, and HPA
- **Security**: Basic security configuration included
- **Monitoring**: Actuator endpoints exposed for monitoring
