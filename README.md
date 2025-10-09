# Database Connection Pool Configuration with HikariCP

A comprehensive Spring Boot application demonstrating high-performance database connection pool configuration using HikariCP, the default connection pool in Spring Boot 2+.

## 🎯 Overview

This project showcases production-ready HikariCP configuration with optimized performance settings, comprehensive monitoring, and health checks. It demonstrates best practices for database connection pooling in enterprise Spring Boot applications.

## 🚀 Features

- **High-Performance Configuration**: Optimized HikariCP settings for maximum throughput
- **Comprehensive Monitoring**: JMX metrics, health checks, and custom actuator endpoints
- **Multi-Database Support**: Configuration examples for MySQL, PostgreSQL, Oracle, SQL Server
- **Production-Ready**: Leak detection, connection validation, and proper timeout handling
- **Performance Testing**: Built-in endpoints for load testing and pool behavior analysis
- **Detailed Documentation**: Extensive comments explaining configuration choices

## 📋 Key Configuration Properties

### Core Pool Settings

- **maximum-pool-size**: 20 (optimized for moderate load)
- **minimum-idle**: 5 (maintains ready connections)

### Timeout Configuration

- **connection-timeout**: 20,000ms (faster than default 30s)
- **max-lifetime**: 1,200,000ms (20 minutes, prevents stale connections)
- **idle-timeout**: 300,000ms (5 minutes idle timeout)
- **validation-timeout**: 5,000ms (5 seconds validation)

### Performance Features

- **leak-detection-threshold**: 60,000ms (1 minute leak detection)
- **JMX monitoring**: Enabled for production observability
- **Connection validation**: Custom test queries for reliability
- **Prepared statement caching**: Optimized for query performance

## 🛠️ Prerequisites

- Java 11 or higher
- Maven 3.6+
- Spring Boot 2.5+

## 📦 Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## 🚀 Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Running the Application

1. **Clone and build the project**:

   ```bash
   mvn clean compile
   ```

2. **Run the application**:

   ```bash
   mvn spring-boot:run
   ```

   Or alternatively, build and run the JAR:

   ```bash
   mvn clean package
   java -jar target/database-connection-pool-1.0.0.jar
   ```

3. **Access the application**:
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health
   - Connection Pool Metrics: http://localhost:8080/actuator/connectionpool

### Maven Commands

- **Clean and compile**: `mvn clean compile`
- **Run tests**: `mvn test`
- **Build JAR**: `mvn clean package`
- **Run application**: `mvn spring-boot:run`
- **Run with production profile**: `mvn spring-boot:run -Pprod`
- **Generate code coverage report**: `mvn clean test jacoco:report`

## 🔍 API Endpoints

### Database Testing

- `GET /test-db` - Test database connectivity
- `GET /load-test` - Simulate database load (10 operations)
- `GET /pool-status` - Detailed connection pool status

### Monitoring Endpoints

- `GET /actuator/health` - Application health with database status
- `GET /actuator/connectionpool` - Custom connection pool metrics
- `GET /actuator/metrics` - Spring Boot metrics

## 📊 Monitoring and Metrics

### Health Check Response

```json
{
  "status": "UP",
  "components": {
    "database": {
      "status": "UP",
      "details": {
        "database": "Available",
        "validationQuery": "SELECT 1",
        "poolName": "HikariCP-Primary",
        "activeConnections": 1,
        "idleConnections": 4,
        "totalConnections": 5
      }
    }
  }
}
```

### Connection Pool Metrics

```json
{
  "poolName": "HikariCP-Primary",
  "activeConnections": 1,
  "idleConnections": 4,
  "totalConnections": 5,
  "maximumPoolSize": 20,
  "poolUtilizationPercentage": 5.0,
  "poolHealthy": true
}
```

## ⚙️ Configuration

### Basic Configuration (application.properties)

```properties
# Core pool settings
app.datasource.hikari.maximum-pool-size=20
app.datasource.hikari.minimum-idle=5

# Timeout configuration
app.datasource.hikari.connection-timeout=20000
app.datasource.hikari.max-lifetime=1200000
app.datasource.hikari.idle-timeout=300000

# Database connection
app.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb
app.datasource.hikari.username=sa
app.datasource.hikari.password=
```

### Database-Specific Configurations

#### MySQL

```properties
app.datasource.hikari.jdbc-url=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
app.datasource.hikari.username=root
app.datasource.hikari.password=password
app.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
```

#### PostgreSQL

```properties
app.datasource.hikari.jdbc-url=jdbc:postgresql://localhost:5432/mydb
app.datasource.hikari.username=postgres
app.datasource.hikari.password=password
app.datasource.hikari.driver-class-name=org.postgresql.Driver
```

## 🎯 Performance Tuning

### Pool Size Calculation

Use the formula: `pool_size = Tn × (Cm - 1) + 1`

- `Tn` = Number of threads
- `Cm` = Number of connections per thread

### Environment-Specific Settings

#### Development

```properties
app.datasource.hikari.maximum-pool-size=10
app.datasource.hikari.leak-detection-threshold=30000
logging.level.com.zaxxer.hikari=DEBUG
```

#### Production

```properties
app.datasource.hikari.maximum-pool-size=50
app.datasource.hikari.minimum-idle=10
app.datasource.hikari.leak-detection-threshold=0
logging.level.com.zaxxer.hikari=WARN
```

#### Load Testing

```properties
app.datasource.hikari.maximum-pool-size=100
app.datasource.hikari.minimum-idle=20
app.datasource.hikari.connection-timeout=10000
```

## 🔧 JVM Optimization

Recommended JVM arguments for optimal performance:

```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-Xms512m
-Xmx2g
-XX:+HeapDumpOnOutOfMemoryError
```

## 📈 Performance Benchmarks

### Connection Pool Efficiency

- **Pool Utilization**: Monitor via `/actuator/connectionpool`
- **Connection Wait Time**: Should be minimal with proper sizing
- **Leak Detection**: Enabled in development, disabled in production

### Key Metrics to Monitor

1. **Active Connections**: Current connections in use
2. **Pool Utilization**: Percentage of pool capacity used
3. **Threads Awaiting Connection**: Should be 0 for optimal performance
4. **Connection Creation Rate**: Lower is better (indicates good pooling)

## 🧪 Testing

### Load Testing

```bash
# Test database connectivity
curl http://localhost:8080/test-db

# Simulate load
curl http://localhost:8080/load-test

# Check pool status
curl http://localhost:8080/pool-status
```

### Health Monitoring

```bash
# Application health
curl http://localhost:8080/actuator/health

# Connection pool metrics
curl http://localhost:8080/actuator/connectionpool
```

## 🔍 Troubleshooting

### Common Issues

1. **Connection Timeouts**

   - Increase `connection-timeout` value
   - Check database connectivity
   - Verify pool size is adequate

2. **Connection Leaks**

   - Enable `leak-detection-threshold` in development
   - Review code for proper connection cleanup
   - Monitor connection pool metrics

3. **Poor Performance**
   - Adjust `maximum-pool-size` based on load
   - Optimize `max-lifetime` and `idle-timeout`
   - Enable prepared statement caching

### Debugging

Enable detailed logging:

```properties
logging.level.com.zaxxer.hikari=DEBUG
logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG
```

## 📚 Best Practices

1. **Pool Sizing**: Start conservative and adjust based on load testing
2. **Timeout Configuration**: Set connection-timeout lower than upstream timeouts
3. **Connection Lifetime**: Configure max-lifetime shorter than database timeout
4. **Monitoring**: Always enable JMX monitoring in production
5. **Leak Detection**: Enable in development, disable in production
6. **Validation**: Use appropriate test queries for your database

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔗 References

- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)
- [Spring Boot Database Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource)
- [Connection Pool Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

**Note**: This implementation uses H2 in-memory database for demonstration. Replace with your production database configuration as needed.
