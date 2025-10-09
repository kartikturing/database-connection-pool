# Database Connection Pool Configuration - LLM Query

## Task Request

Please create a high-performance database connection pool implementation using HikariCP (the default connection pool in Spring Boot 2+) in a Spring Boot application. The implementation should focus on optimizing database connectivity through proper configuration of key performance and reliability properties, comprehensive testing, and production-ready monitoring.

## Requirements to Implement

### 1. **HikariCP Configuration Properties**

Please configure HikariCP with the following properties:

- **Production Configuration** (`application.properties`):

  - `maximum-pool-size`: 20 connections for high-throughput applications
  - `minimum-idle`: 5 connections to maintain baseline availability
  - `connection-timeout`: 20000ms (20 seconds) for reasonable wait times
  - `max-lifetime`: 1200000ms (20 minutes) for connection freshness
  - `idle-timeout`: 300000ms (5 minutes) for efficient resource usage
  - `leak-detection-threshold`: 60000ms (1 minute) for development debugging
  - `validation-timeout`: 5000ms for quick connection validation
  - `auto-commit`: true for default transaction behavior
  - `connection-test-query`: "SELECT 1" for connection validation
  - `register-mbeans`: true for JMX monitoring
  - `pool-name`: "HikariCP-Production" for identification

- **Test Configuration** (`application-test.properties`):
  - Optimize for faster test execution with smaller pool sizes
  - `maximum-pool-size`: 5, `minimum-idle`: 2
  - Reduce timeouts for quicker test feedback

### 2. **Spring Boot Integration**

Please implement the following Spring Boot integration:

- **Single File Architecture**: Consolidate all components in `DatabaseConnectionPoolApplication.java`
- **Configuration Properties Binding**: Use `@ConfigurationProperties("spring.datasource.hikari")`
- **Auto-Configuration**: Leverage Spring Boot's HikariCP auto-configuration
- **Component Organization**: Create the following components:
  - `HikariProperties`: Configuration properties class
  - `DatabaseConfig`: HikariDataSource bean configuration
  - `DatabaseHealthIndicator`: Custom health monitoring
  - `ConnectionPoolEndpoint`: Custom actuator endpoint
  - `DatabaseTestController`: REST endpoints for testing

### 3. **Database Integration**

Please set up database integration with:

- **H2 In-Memory Database**: For development and testing
- **JDBC Template Integration**: For database operations
- **Connection Validation**: Implement automatic connection health checks
- **Multi-Environment Support**: Create separate configurations for production and test

### 4. **Monitoring and Diagnostics**

Please implement monitoring and diagnostics with:

- **JMX Monitoring**: Enable for production monitoring
- **Custom Health Indicator**: Create `/actuator/health` endpoint with database status
- **Custom Actuator Endpoint**: Implement `/actuator/connectionpool` with detailed metrics
- **Connection Pool Metrics**: Include the following metrics:
  - Active connections count
  - Total connections count
  - Pool utilization percentage
  - Pool health status
  - Pool name and configuration details

### 5. **REST API Endpoints**

Please create the following REST API endpoints:

- **`/test-db`**: Database connectivity test with connection details
- **`/load-test`**: Connection pool load testing (10 concurrent operations)
- **`/pool-status`**: Detailed pool status and utilization metrics
- **`/actuator/health`**: Spring Boot health endpoint with database status
- **`/actuator/connectionpool`**: Custom endpoint with comprehensive pool metrics

### 6. **Comprehensive Test Suite**

Please create a comprehensive test suite with **25 Test Methods Organized in 7 Categories:**

1. **HikariCP Properties Tests** (4 tests)

   - Default pool size settings validation
   - Default timeout settings validation
   - Default reliability settings validation
   - Property setters/getters validation

2. **Database Configuration Tests** (5 tests)

   - HikariDataSource bean creation
   - Configuration properties validation
   - Database connection establishment
   - Database operations execution
   - Multiple concurrent connections handling

3. **Health Indicator Tests** (3 tests)

   - Healthy database status reporting
   - HikariCP metrics inclusion in health details
   - Unhealthy database status handling

4. **REST Controller Tests** (4 tests)

   - Database connectivity endpoint testing
   - Load test endpoint validation
   - Pool status endpoint verification
   - Concurrent request handling

5. **Actuator Endpoint Tests** (5 tests)

   - Connection pool endpoint availability
   - Comprehensive metrics validation
   - Health endpoint integration
   - Database health component verification
   - Direct endpoint method testing

6. **Performance Tests** (5 tests)

   - Concurrent connection request efficiency
   - Connection pool exhaustion handling
   - Connection reuse efficiency demonstration
   - Rapid connection cycling performance
   - Mixed workload pool health maintenance

7. **Integration Tests** (2 tests)
   - Complete application integration validation
   - Configuration consistency across components

### 7. **Performance Optimizations**

Please implement the following performance optimizations:

- **Pool Size Calculation**: Optimize for typical web application loads
- **Connection Lifetime Management**: Balance for performance and resource efficiency
- **Timeout Configuration**: Prevent resource starvation while maintaining responsiveness
- **Leak Detection**: Enable for development debugging
- **Connection Validation**: Ensure connection reliability

### 8. **Production Readiness Features**

Please ensure the following production readiness features:

- **Environment-Specific Configuration**: Create separate settings for production and test
- **Comprehensive Monitoring**: Implement health checks, metrics, and JMX integration
- **Error Handling**: Provide graceful degradation and proper error reporting
- **Resource Management**: Ensure efficient connection pooling and cleanup
- **Observability**: Include detailed logging and metrics collection

## Technical Implementation Specifications

### **Maven Configuration** (`pom.xml`)

Please set up Maven configuration with:

- Spring Boot 3.2.0 parent
- Dependencies: Web, JDBC, Actuator, HikariCP, H2, JUnit 5, Mockito
- Java 17 compatibility
- Maven Surefire Plugin for testing

### **Testing Strategy**

Please implement the following testing strategy:

- **Unit Tests**: Individual component validation
- **Integration Tests**: End-to-end application testing
- **Performance Tests**: Connection pool behavior under load
- **HTTP Tests**: REST endpoint validation with TestRestTemplate
- **Mock Testing**: Error condition simulation

### **Build and Validation**

Please provide:

- **Maven Build System**: Complete project lifecycle management
- **Automated Testing**: Comprehensive test suite execution
- **Validation Script**: `run_and_validate.sh` for complete project validation
- **Continuous Integration Ready**: All tests automated and reproducible

## Expected Skills to Demonstrate

The implementation should demonstrate:

1. **Database Connection Pooling**: Advanced HikariCP configuration and optimization
2. **Spring Boot Mastery**: Auto-configuration, properties binding, actuator integration
3. **Performance Tuning**: Optimal settings for high-performance applications
4. **Production Readiness**: Monitoring, health checks, and observability
5. **Testing Excellence**: Comprehensive test coverage with multiple testing strategies
6. **Resource Management**: Efficient database resource utilization
7. **API Design**: RESTful endpoints for testing and monitoring
8. **Configuration Management**: Environment-specific settings and property binding

## Expected Project Structure

Please create the following project structure:

```
database-connection-pool/
├── pom.xml
├── detailed-prompt.md
├── run_and_validate.sh
├── src/
│   ├── main/
│   │   ├── java/com/example/connectionpool/
│   │   │   └── DatabaseConnectionPoolApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/example/connectionpool/
│       │   └── DatabaseConnectionPoolApplicationTest.java
│       └── resources/
│           └── application-test.properties
└── target/ (generated)
```

## Deliverable

Please provide a production-ready, high-performance database connection pool with comprehensive testing, monitoring, and optimization features that demonstrate enterprise-level Spring Boot application development skills.
