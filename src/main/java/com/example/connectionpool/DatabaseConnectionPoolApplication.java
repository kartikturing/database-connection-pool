package com.example.connectionpool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot Application demonstrating high-performance HikariCP database connection pool configuration.
 * <p>
 * This application showcases:
 * 1. Programmatic HikariCP configuration with optimized settings
 * 2. Custom configuration properties binding
 * 3. Database health monitoring and diagnostics
 * 4. Connection pool metrics and monitoring endpoints
 * 5. Production-ready connection pool setup
 * <p>
 * Key Performance Features:
 * - Optimized pool sizing based on application load
 * - Proper timeout configuration for reliability
 * - Connection leak detection for debugging
 * - JMX monitoring for production observability
 * - Custom validation queries for connection health
 *
 * @author Database Performance Team
 * @version 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties(DatabaseConnectionPoolApplication.HikariProperties.class)
public class DatabaseConnectionPoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseConnectionPoolApplication.class, args);
    }

    /**
     * Custom configuration properties for HikariCP.
     * These properties can be overridden in application.properties or application.yml
     */
    @ConfigurationProperties(prefix = "app.datasource.hikari")
    public static class HikariProperties {

        // Core pool settings
        private int maximumPoolSize = 20;           // Optimized for moderate load
        private int minimumIdle = 5;                // Maintain minimum connections

        // Timeout configuration (in milliseconds)
        private long connectionTimeout = 20000;     // 20 seconds - faster than default
        private long maxLifetime = 1200000;         // 20 minutes - shorter than default
        private long idleTimeout = 300000;          // 5 minutes idle timeout
        private long validationTimeout = 5000;      // 5 seconds validation

        // Performance and reliability settings
        private long leakDetectionThreshold = 60000; // 1 minute leak detection
        private boolean autoCommit = true;
        private String connectionTestQuery = "SELECT 1";

        // JMX and monitoring
        private boolean registerMbeans = true;
        private String poolName = "HikariCP-Primary";

        // Database connection settings
        private String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        private String username = "sa";
        private String password = "";
        private String driverClassName = "org.h2.Driver";

        // Getters and setters
        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getMinimumIdle() {
            return minimumIdle;
        }

        public void setMinimumIdle(int minimumIdle) {
            this.minimumIdle = minimumIdle;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public long getMaxLifetime() {
            return maxLifetime;
        }

        public void setMaxLifetime(long maxLifetime) {
            this.maxLifetime = maxLifetime;
        }

        public long getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(long idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public long getValidationTimeout() {
            return validationTimeout;
        }

        public void setValidationTimeout(long validationTimeout) {
            this.validationTimeout = validationTimeout;
        }

        public long getLeakDetectionThreshold() {
            return leakDetectionThreshold;
        }

        public void setLeakDetectionThreshold(long leakDetectionThreshold) {
            this.leakDetectionThreshold = leakDetectionThreshold;
        }

        public boolean isAutoCommit() {
            return autoCommit;
        }

        public void setAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
        }

        public String getConnectionTestQuery() {
            return connectionTestQuery;
        }

        public void setConnectionTestQuery(String connectionTestQuery) {
            this.connectionTestQuery = connectionTestQuery;
        }

        public boolean isRegisterMbeans() {
            return registerMbeans;
        }

        public void setRegisterMbeans(boolean registerMbeans) {
            this.registerMbeans = registerMbeans;
        }

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }

    /**
     * Database configuration class that creates and configures the HikariCP DataSource.
     */
    @Configuration
    public static class DatabaseConfig {

        private final HikariProperties hikariProperties;

        public DatabaseConfig(HikariProperties hikariProperties) {
            this.hikariProperties = hikariProperties;
        }

        /**
         * Creates a high-performance HikariCP DataSource with optimized configuration.
         * <p>
         * Configuration Rationale:
         * - maximumPoolSize: Set based on expected concurrent database operations
         * - minimumIdle: Maintains ready connections for immediate use
         * - connectionTimeout: Prevents long waits for connections
         * - maxLifetime: Ensures connection freshness and prevents stale connections
         * - leakDetectionThreshold: Helps identify connection leaks in development
         *
         * @return Configured HikariDataSource
         */
        @Bean
        @Primary
        public DataSource dataSource() {
            HikariConfig config = new HikariConfig();

            // Database connection settings
            config.setJdbcUrl(hikariProperties.getJdbcUrl());
            config.setUsername(hikariProperties.getUsername());
            config.setPassword(hikariProperties.getPassword());
            config.setDriverClassName(hikariProperties.getDriverClassName());

            // Core pool configuration
            config.setMaximumPoolSize(hikariProperties.getMaximumPoolSize());
            config.setMinimumIdle(hikariProperties.getMinimumIdle());

            // Timeout configuration
            config.setConnectionTimeout(hikariProperties.getConnectionTimeout());
            config.setMaxLifetime(hikariProperties.getMaxLifetime());
            config.setIdleTimeout(hikariProperties.getIdleTimeout());
            config.setValidationTimeout(hikariProperties.getValidationTimeout());

            // Performance and reliability settings
            config.setLeakDetectionThreshold(hikariProperties.getLeakDetectionThreshold());
            config.setAutoCommit(hikariProperties.isAutoCommit());
            config.setConnectionTestQuery(hikariProperties.getConnectionTestQuery());

            // Monitoring and JMX
            config.setRegisterMbeans(hikariProperties.isRegisterMbeans());
            config.setPoolName(hikariProperties.getPoolName());

            // Additional performance optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            return new HikariDataSource(config);
        }

        /**
         * JdbcTemplate bean for database operations
         */
        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }

    /**
     * Health indicator for database connectivity monitoring.
     * Provides detailed health information about the database connection pool.
     */
    @Component
    public static class DatabaseHealthIndicator implements HealthIndicator {

        private final DataSource dataSource;
        private final JdbcTemplate jdbcTemplate;

        public DatabaseHealthIndicator(DataSource dataSource, JdbcTemplate jdbcTemplate) {
            this.dataSource = dataSource;
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public Health health() {
            try {
                // Test database connectivity
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);

                // Get HikariCP metrics if available
                Health.Builder builder = Health.up()
                        .withDetail("database", "Available")
                        .withDetail("validationQuery", "SELECT 1");

                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                    builder.withDetail("poolName", hikariDataSource.getPoolName())
                            .withDetail("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections())
                            .withDetail("idleConnections", hikariDataSource.getHikariPoolMXBean().getIdleConnections())
                            .withDetail("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections())
                            .withDetail("threadsAwaitingConnection", hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
                }

                return builder.build();

            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "Unavailable")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }

    /**
     * Custom actuator endpoint for detailed connection pool metrics.
     */
    @Component
    @Endpoint(id = "connectionpool")
    public static class ConnectionPoolEndpoint {

        private final DataSource dataSource;

        public ConnectionPoolEndpoint(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @ReadOperation
        public Map<String, Object> connectionPoolMetrics() {
            Map<String, Object> metrics = new HashMap<>();

            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

                metrics.put("poolName", hikariDataSource.getPoolName());
                metrics.put("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                metrics.put("idleConnections", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
                metrics.put("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
                metrics.put("threadsAwaitingConnection", hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
                metrics.put("maximumPoolSize", hikariDataSource.getMaximumPoolSize());
                metrics.put("minimumIdle", hikariDataSource.getMinimumIdle());
                metrics.put("connectionTimeout", hikariDataSource.getConnectionTimeout());
                metrics.put("maxLifetime", hikariDataSource.getMaxLifetime());
                metrics.put("idleTimeout", hikariDataSource.getIdleTimeout());

                // Connection usage statistics
                double utilizationPercentage = (double) hikariDataSource.getHikariPoolMXBean().getActiveConnections()
                        / hikariDataSource.getMaximumPoolSize() * 100;
                metrics.put("poolUtilizationPercentage", Math.round(utilizationPercentage * 100.0) / 100.0);

                // Health status
                boolean isHealthy = hikariDataSource.getHikariPoolMXBean().getActiveConnections() >= 0;
                metrics.put("poolHealthy", isHealthy);

            } else {
                metrics.put("error", "DataSource is not HikariDataSource");
            }

            return metrics;
        }
    }

    /**
     * REST controller for testing database connectivity and demonstrating pool usage.
     */
    @RestController
    public static class DatabaseTestController {

        private final JdbcTemplate jdbcTemplate;
        private final DataSource dataSource;

        public DatabaseTestController(JdbcTemplate jdbcTemplate, DataSource dataSource) {
            this.jdbcTemplate = jdbcTemplate;
            this.dataSource = dataSource;
        }

        /**
         * Test endpoint to verify database connectivity
         */
        @GetMapping("/test-db")
        public Map<String, Object> testDatabase() {
            Map<String, Object> result = new HashMap<>();

            try {
                // Test basic connectivity
                Integer testResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                result.put("connectionTest", "SUCCESS");
                result.put("testQuery", "SELECT 1");
                result.put("result", testResult);

                // Get connection pool information
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                    result.put("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                    result.put("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
                    result.put("poolName", hikariDataSource.getPoolName());
                }

                result.put("timestamp", System.currentTimeMillis());

            } catch (Exception e) {
                result.put("connectionTest", "FAILED");
                result.put("error", e.getMessage());
            }

            return result;
        }

        /**
         * Endpoint to simulate database load for testing pool behavior
         */
        @GetMapping("/load-test")
        public Map<String, Object> loadTest() {
            Map<String, Object> result = new HashMap<>();
            long startTime = System.currentTimeMillis();

            try {
                // Simulate multiple database operations
                for (int i = 0; i < 10; i++) {
                    jdbcTemplate.queryForObject("SELECT " + (i + 1), Integer.class);
                }

                long endTime = System.currentTimeMillis();
                result.put("loadTest", "SUCCESS");
                result.put("operations", 10);
                result.put("executionTimeMs", endTime - startTime);

                // Pool statistics after load
                if (dataSource instanceof HikariDataSource) {
                    HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                    result.put("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                    result.put("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
                }

            } catch (Exception e) {
                result.put("loadTest", "FAILED");
                result.put("error", e.getMessage());
            }

            return result;
        }

        /**
         * Endpoint to get detailed connection pool status
         */
        @GetMapping("/pool-status")
        public Map<String, Object> getPoolStatus() {
            Map<String, Object> status = new HashMap<>();

            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

                status.put("poolName", hikariDataSource.getPoolName());
                status.put("jdbcUrl", hikariDataSource.getJdbcUrl());
                status.put("maximumPoolSize", hikariDataSource.getMaximumPoolSize());
                status.put("minimumIdle", hikariDataSource.getMinimumIdle());
                status.put("activeConnections", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                status.put("idleConnections", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
                status.put("totalConnections", hikariDataSource.getHikariPoolMXBean().getTotalConnections());
                status.put("threadsAwaitingConnection", hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());

                // Configuration details
                status.put("connectionTimeout", hikariDataSource.getConnectionTimeout());
                status.put("maxLifetime", hikariDataSource.getMaxLifetime());
                status.put("idleTimeout", hikariDataSource.getIdleTimeout());
                status.put("leakDetectionThreshold", hikariDataSource.getLeakDetectionThreshold());

                // Calculate pool efficiency metrics
                double utilization = (double) hikariDataSource.getHikariPoolMXBean().getActiveConnections()
                        / hikariDataSource.getMaximumPoolSize();
                status.put("poolUtilization", Math.round(utilization * 10000.0) / 100.0 + "%");

                boolean isOptimal = hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection() == 0
                        && hikariDataSource.getHikariPoolMXBean().getActiveConnections() > 0;
                status.put("performanceOptimal", isOptimal);

            } else {
                status.put("error", "DataSource is not HikariDataSource");
            }

            status.put("timestamp", System.currentTimeMillis());
            return status;
        }
    }
}

/*
 * APPLICATION.PROPERTIES CONFIGURATION EXAMPLE:
 *
 * # HikariCP Configuration
 * app.datasource.hikari.maximum-pool-size=20
 * app.datasource.hikari.minimum-idle=5
 * app.datasource.hikari.connection-timeout=20000
 * app.datasource.hikari.max-lifetime=1200000
 * app.datasource.hikari.idle-timeout=300000
 * app.datasource.hikari.validation-timeout=5000
 * app.datasource.hikari.leak-detection-threshold=60000
 * app.datasource.hikari.auto-commit=true
 * app.datasource.hikari.connection-test-query=SELECT 1
 * app.datasource.hikari.register-mbeans=true
 * app.datasource.hikari.pool-name=HikariCP-Primary
 *
 * # Database Configuration (H2 for demo, replace with your database)
 * app.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
 * app.datasource.hikari.username=sa
 * app.datasource.hikari.password=
 * app.datasource.hikari.driver-class-name=org.h2.Driver
 *
 * # For MySQL:
 * # app.datasource.hikari.jdbc-url=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
 * # app.datasource.hikari.username=root
 * # app.datasource.hikari.password=password
 * # app.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
 * # app.datasource.hikari.connection-test-query=SELECT 1
 *
 * # For PostgreSQL:
 * # app.datasource.hikari.jdbc-url=jdbc:postgresql://localhost:5432/mydb
 * # app.datasource.hikari.username=postgres
 * # app.datasource.hikari.password=password
 * # app.datasource.hikari.driver-class-name=org.postgresql.Driver
 * # app.datasource.hikari.connection-test-query=SELECT 1
 *
 * # Actuator endpoints
 * management.endpoints.web.exposure.include=health,info,connectionpool
 * management.endpoint.health.show-details=always
 *
 * # Logging configuration
 * logging.level.com.zaxxer.hikari=DEBUG
 * logging.level.com.zaxxer.hikari.HikariConfig=DEBUG
 * logging.level.com.zaxxer.hikari.pool.HikariPool=DEBUG
 */

/*
 * MAVEN DEPENDENCIES REQUIRED:
 *
 * <dependencies>
 *     <dependency>
 *         <groupId>org.springframework.boot</groupId>
 *         <artifactId>spring-boot-starter-web</artifactId>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework.boot</groupId>
 *         <artifactId>spring-boot-starter-jdbc</artifactId>
 *     </dependency>
 *     <dependency>
 *         <groupId>org.springframework.boot</groupId>
 *         <artifactId>spring-boot-starter-actuator</artifactId>
 *     </dependency>
 *     <dependency>
 *         <groupId>com.zaxxer</groupId>
 *         <artifactId>HikariCP</artifactId>
 *     </dependency>
 *     <dependency>
 *         <groupId>com.h2database</groupId>
 *         <artifactId>h2</artifactId>
 *         <scope>runtime</scope>
 *     </dependency>
 * </dependencies>
 */
