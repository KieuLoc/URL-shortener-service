# GitHub Actions CI/CD Pipeline

This repository uses GitHub Actions for continuous integration and deployment.

## Workflows

### CI/CD Pipeline (`.github/workflows/ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### 1. Test and Lint
- **Platform:** Ubuntu Latest
- **Java Version:** 17 (Temurin)
- **Steps:**
  - Checkout code
  - Setup JDK 17 with Maven cache
  - Run Maven clean compile
  - Run Maven test
  - Run Maven package
  - Run SpotBugs static analysis
  - Run Checkstyle linting
  - Upload test results
  - Upload SpotBugs results
  - Upload Checkstyle results

#### 2. Docker Build (Only on main branch)
- **Platform:** Ubuntu Latest
- **Prerequisites:** Test job must pass
- **Steps:**
  - Checkout code
  - Setup Docker Buildx
  - Build Docker image
  - Test Docker image (health check)

## Tools Used

### Static Analysis
- **SpotBugs:** Find bugs in Java code
- **Checkstyle:** Code style and formatting
- **Maven:** Build automation

### Testing
- **JUnit 5:** Unit testing
- **MockMvc:** Integration testing
- **Maven Surefire:** Test execution

### Containerization
- **Docker:** Application containerization
- **Health Checks:** Verify container functionality

## Artifacts

The pipeline generates the following artifacts:
- Test results (surefire-reports)
- SpotBugs analysis results
- Checkstyle analysis results

## Configuration

### Maven Plugins
- `maven-compiler-plugin`: Java 17 compilation
- `maven-surefire-plugin`: Unit test execution
- `spotbugs-maven-plugin`: Static analysis
- `maven-checkstyle-plugin`: Code style checking
- `spring-boot-maven-plugin`: Spring Boot packaging

### Checkstyle
- Configuration: `checkstyle.xml`
- Based on Google Java Style Guide
- Customized for project needs

## Local Development

To run the same checks locally:

```bash
# Run tests
mvn test

# Run static analysis
mvn spotbugs:check
mvn checkstyle:check

# Build application
mvn clean package

# Build Docker image
docker build -t url-shortener .
```

## Troubleshooting

### Common Issues

1. **Checkstyle failures**: Check `target/checkstyle-result.xml` for details
2. **SpotBugs failures**: Check `target/spotbugsXml.xml` for details
3. **Test failures**: Check `target/surefire-reports/` for test results
4. **Docker build failures**: Ensure Dockerfile is valid and dependencies are available

### Debug Mode

To enable debug logging in GitHub Actions, add this to your workflow:

```yaml
- name: Run Maven with debug
  run: mvn clean test -X
```
