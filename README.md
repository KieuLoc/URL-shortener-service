# 🚀 URL Shortener Service

A modern, scalable URL shortener service built with **Java Spring Boot** and **Thymeleaf** for the frontend. Features in-memory caching, Docker containerization, GitHub Actions CI/CD, and optional Kubernetes deployment.

## ✨ **Features**

- 🔗 **URL Shortening**: Convert long URLs to short, memorable links
- 🎯 **Smart Redirects**: HTTP 302 redirects for optimal SEO
- 💾 **Redis Cache**: Persistent, scalable storage with TTL support
- 🌐 **Web Interface**: Beautiful, responsive Thymeleaf GUI
- 🐳 **Docker Ready**: Complete containerization with Redis + App
- 🚀 **CI/CD Pipeline**: GitHub Actions for testing, linting, and deployment
- ☸️ **Kubernetes Support**: Production-ready K8s configurations
- 🔒 **Security**: Spring Security with public access configuration
- 📊 **Health Monitoring**: Spring Boot Actuator endpoints
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

### **4. Run Locally (In-Memory Cache)**
```bash
# Build the project
./mvnw clean package

# Run with in-memory cache (no Redis needed)
./mvnw spring-boot:run -Dspring.profiles.active=default

# Access the application
open http://localhost:8080
```

## 📖 **Usage**

### **Web Interface**
1. **Navigate** to `http://localhost:8080`
2. **Enter** your long URL in the input field
3. **Click** "Shorten URL" button
4. **Copy** the generated short URL
5. **Test** the short URL to verify redirection

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
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build

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

### **Access**
```bash
# NodePort access
curl http://<NODE_IP>:30080/

# Ingress access (add to /etc/hosts)
echo "127.0.0.1 url-shortener.local" | sudo tee -a /etc/hosts
curl http://url-shortener.local/
```

For detailed Kubernetes instructions, see [k8s/README.md](k8s/README.md).

## 🧪 **Testing**

### **Run Tests**
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=InMemoryUrlShortenerServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### **Test Categories**
- **Unit Tests**: Service layer logic
- **Integration Tests**: Controller endpoints
- **Web Tests**: Thymeleaf templates
- **API Tests**: REST endpoints

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

### **Code Quality**
```bash
# Run SpotBugs
./mvnw spotbugs:check

# Run Checkstyle
./mvnw checkstyle:check

# Run PMD
./mvnw pmd:check

# Run OWASP Dependency Check
./mvnw dependency-check:check
```

### **Build Commands**
```bash
# Clean build
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests

# Run specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📊 **Monitoring**

### **Health Endpoints**
- **Health**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`
- **Prometheus**: `GET /actuator/prometheus`

### **Logs**
```bash
# Application logs
docker-compose logs -f app

# Kubernetes logs
kubectl logs -n url-shortener deployment/url-shortener-app -f
```

## 🔒 **Security**

- **Public Access**: No authentication required for basic functionality
- **CSRF Protection**: Disabled for API endpoints
- **Frame Options**: Disabled for embedding support
- **Input Validation**: URL format validation
- **Error Handling**: Graceful error responses

## 🚀 **CI/CD Pipeline**

### **GitHub Actions**
- **Trigger**: Push/PR to main/develop branches
- **Jobs**: Test, Lint, Package, Docker Build
- **Tools**: SpotBugs, Checkstyle, JUnit, Docker
- **Artifacts**: Test results, linting reports

### **Pipeline Steps**
1. **Checkout** code
2. **Setup** JDK 17
3. **Run** tests
4. **Execute** linting (SpotBugs, Checkstyle)
5. **Package** application
6. **Build** Docker image
7. **Upload** artifacts

## 📝 **Configuration**

### **Application Properties**
```yaml
# Server configuration
server:
  port: 8080

# Spring profiles
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:default}

# Redis configuration
spring:
  redis:
    host: localhost        # or 'redis' in Docker
    port: 6379
    database: 0
    timeout: 2000
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### **Environment Variables**
- `SPRING_PROFILES_ACTIVE`: Spring profile (default, docker, k8s)
- `BASE_URL`: Base URL for short URLs
- `SERVER_PORT`: Application port
- `SPRING_REDIS_HOST`: Redis server host
- `SPRING_REDIS_PORT`: Redis server port
- `LOGGING_LEVEL_*`: Logging configuration

### **Cache Options**
- **Redis** (default): Persistent, scalable, production-ready
- **In-Memory**: Simple, no external dependencies

## 🐛 **Troubleshooting**

### **Common Issues**

1. **Port 8080 already in use**
   ```bash
   # Find and kill process
   netstat -ano | findstr :8080
   taskkill /F /PID <PID>
   ```

2. **Redis connection failed**
   ```bash
   # Check Redis is running
   docker exec -it url-shortener-redis redis-cli ping
   
   # Start Redis if not running
   docker-compose up redis -d
   
   # Check Redis logs
   docker-compose logs redis
   ```

3. **Docker daemon not running**
   ```bash
   # Start Docker Desktop
   # Wait for full initialization
   ```

4. **Application not accessible**
   ```bash
   # Check if containers are running
   docker-compose ps
   
   # Check app logs
   docker-compose logs app
   
   # Check Redis logs
   docker-compose logs redis
   ```

5. **Kubernetes deployment issues**
   ```bash
   # Check pod status
   kubectl get pods -n url-shortener
   
   # Describe pod for details
   kubectl describe pod <POD_NAME> -n url-shortener
   ```

### **Debug Commands**
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Test URL shortening
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com"}'

# Check Redis connection
docker exec -it url-shortener-redis redis-cli ping

# View Redis data
docker exec -it url-shortener-redis redis-cli
# In Redis CLI: KEYS * (see all keys)
# In Redis CLI: GET url:abc123 (get specific URL)

# View application logs
docker-compose logs -f app

# View Redis logs
docker-compose logs -f redis
```

## 🤝 **Contributing**

1. **Fork** the repository
2. **Create** a feature branch
3. **Commit** your changes
4. **Push** to the branch
5. **Create** a Pull Request

### **Development Guidelines**
- Follow Java coding standards
- Write comprehensive tests
- Update documentation
- Ensure CI/CD pipeline passes

## 🙏 **Acknowledgments**

- **Spring Boot** team for the excellent framework
- **Thymeleaf** for the template engine
- **Bootstrap** for the responsive UI
- **Docker** for containerization
- **Kubernetes** for orchestration

---