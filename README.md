# 🚀 URL Shortener Service

A modern, scalable URL shortener service built with **Java Spring Boot** and **Thymeleaf** for the frontend. Features in-memory caching, Docker containerization, GitHub Actions CI/CD, and optional Kubernetes deployment.

## ✨ **Features**

- 🔗 **URL Shortening**: Convert long URLs to short, memorable links
- 🎯 **Smart Redirects**: HTTP 302 redirects for optimal SEO
- 💾 **Redis Cache**: Persistent, scalable storage with TTL support
- 📊 **Basic Analytics**: Track clicks, view top URLs, and recent activity
- 🌐 **Web Interface**: Beautiful, responsive Thymeleaf GUI
- 🐳 **Docker Ready**: Complete containerization with Redis + App
- 🚀 **CI/CD Pipeline**: GitHub Actions for testing, linting, and deployment
- ☸️ **Kubernetes Support**: Production-ready K8s configurations
- 🔒 **Security**: Spring Security with public access configuration
- 📈 **Health Monitoring**: Spring Boot Actuator endpoints
- 🧪 **Testing**: Comprehensive unit and integration tests
- ⚡ **High Performance**: Sub-millisecond Redis operations

## 🛠 **Tech Stack**

- **Backend**: Java 17, Spring Boot 3.2+
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Cache**: Redis 7, Jedis Client
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Orchestration**: Kubernetes (optional)
- **Testing**: JUnit 5, MockMvc, Spring Boot Test
- **Code Quality**: SpotBugs, Checkstyle, PMD, OWASP Dependency Check

## 🚀 **Quick Start**

### **Prerequisites**
- **Java 17+**
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Redis** (optional - included in Docker Compose)

### **1. Clone Repository**
```bash
git clone <repository-url>
cd shorten-url
```

### **2. Run with Docker (Recommended)**
```bash
# Build and start Redis + App
docker-compose up --build

# Check Redis is running
docker exec -it url-shortener-redis redis-cli ping
# Should return: PONG

# Access the application
open http://localhost:8080
```

### **3. Run Locally with Redis**
```bash
# Start Redis (if not using Docker)
redis-server

# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Access the application
open http://localhost:8080
```

### **API Endpoints**
```bash
# Shorten URL (POST)
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com", "expirationDays": 30}'

# Redirect (GET)
curl -I http://localhost:8080/{shortCode}

# Health Check
curl http://localhost:8080/actuator/health
```

## 🐳 **Docker Deployment**

### **Development**
```bash
# Start Redis + App with hot reload
docker-compose -f docker-compose.yml -up --build

# View app logs
docker-compose logs -f app

# View Redis logs
docker-compose logs -f redis

# Check Redis data
docker exec -it url-shortener-redis redis-cli
# In Redis CLI: KEYS * (to see all keys)
```

### **Production**
```bash
# Start production services
docker-compose up --build -d

# Check status
docker-compose ps

# Check Redis persistence
docker exec -it url-shortener-redis redis-cli ping

# Stop services
docker-compose down

# Stop and remove volumes (WARNING: deletes Redis data)
docker-compose down -v
```

### **Docker Commands**
```bash
# Build image
docker build -t url-shortener:latest .

# Run container with Redis
docker run -p 8080:8080 \
  -e SPRING_REDIS_HOST=host.docker.internal \
  -e SPRING_REDIS_PORT=6379 \
  url-shortener:latest

# Run Redis separately
docker run -d --name redis -p 6379:6379 redis:7-alpine

# View logs
docker logs <container-id>

# Execute shell
docker exec -it <container-id> /bin/sh

# Redis CLI
docker exec -it redis redis-cli
```

## ☸️ **Kubernetes Deployment**

### **Prerequisites**
- Kubernetes cluster (v1.20+)
- kubectl configured
- Ingress controller (nginx recommended)

### **Deploy**
```bash
# Deploy all resources
kubectl apply -k k8s/

# Check deployment
kubectl get pods -n url-shortener

# Access via NodePort
kubectl get svc -n url-shortener
```

## 🔧 **Development**

### **Project Structure**
```
src/
├── main/
│   ├── java/com/urlshortener/
│   │   ├── controller/     # REST & Web controllers
│   │   ├── service/        # Business logic
│   │   ├── dto/           # Data transfer objects
│   │   ├── util/          # Utilities
│   │   └── config/        # Configuration
│   └── resources/
│       ├── templates/     # Thymeleaf templates
│       └── application.yml # Configuration
└── test/
    └── java/com/urlshortener/
        ├── controller/    # Integration tests
        └── service/       # Unit tests
```