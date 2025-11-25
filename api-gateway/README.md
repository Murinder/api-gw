# API Gateway for ECOP Platform

## Overview
This is the API Gateway service for the ECOP (Единая Цифровая Образовательная Платформа Университета) Platform. It serves as the single entry point for all client requests and provides authentication, rate limiting, circuit breaking, and routing to appropriate microservices.

## Features
- Authentication and authorization with JWT validation
- Rate limiting using Redis
- Circuit breaker pattern with Resilience4j
- Service discovery with Consul
- Request routing to backend services
- Cross-origin resource sharing (CORS)
- Health checks and monitoring
- Distributed tracing with Sleuth and Zipkin
- Request aggregation from multiple services
- Security filters for SQL injection and XSS protection
- Response caching with Redis and local cache
- Advanced logging and monitoring

## Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Cloud Gateway
- Spring Security
- Redis for rate limiting and caching
- Consul for service discovery
- Resilience4j for circuit breaking
- Sleuth and Zipkin for distributed tracing
- Maven for build management

## Configuration
All configuration values can be overridden using environment variables. See the `.env.example` file for all available options.

### Required Environment Variables
- `SERVER_PORT`: Port on which the gateway will run (default: 8080)
- `APP_NAME`: Application name for service discovery (default: api-gateway)
- `REDIS_HOST`: Redis server host (default: localhost)
- `REDIS_PORT`: Redis server port (default: 6379)
- `CONSUL_HOST`: Consul server host (default: consul)
- `CONSUL_PORT`: Consul server port (default: 8500)

## Running Locally
1. Create a `.env` file with your environment variables
2. Build the project: `mvn clean package`
3. Run the application: `java -jar target/api-gateway-*.jar`

## Running with Docker
1. Build the image: `docker build -t api-gateway .`
2. Run the container: `docker run -p 8080:8080 api-gateway`

## Environment Variables (.env)
Create a `.env` file with the following variables:

```
SERVER_PORT=8080
APP_NAME=api-gateway
REDIS_HOST=localhost
REDIS_PORT=6379
CONSUL_HOST=localhost
CONSUL_PORT=8500
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
JWT_SECRET=your-super-secret-jwt-key
LOG_LEVEL=INFO
ZIPKIN_URL=http://zipkin:9411/
```

## API Routes
The gateway routes requests to the following services:

### Core Service Authentication and Authorization:
- `/api/auth/login` - User login
- `/api/auth/register` - User registration
- `/api/auth/verify` - Email verification
- `/api/auth/reset-password` - Password reset
- `/api/auth/refresh-token` - Token refresh
- `/api/auth/validate` - Token validation

### Core Service User Management:
- `/api/users/profile` - Get/update current user profile
- `/api/users/{id}` - Get specific user profile
- `/api/users/search` - Search users
- `/api/users/skills` - Search users by skills
- `/api/users/connections` - Manage user connections

### Core Service Other Endpoints:
- `/api/dashboards/**` - Dashboard management
- `/api/notifications/**` - Notification management
- `/api/search/**` - Global search
- `/api/chats/**` - Communication endpoints

### Other Services:
- Project Service: `/api/projects/**`
- Events Service: `/api/events/**`
- Portfolio Service: `/api/portfolios/**`
- Analytics Service: `/api/analytics/**`

## Security Features
- JWT token validation
- SQL injection protection
- XSS attack prevention
- Rate limiting per user/IP
- Circuit breaker for service failures

## Health Checks
The application provides health check endpoints:
- `/actuator/health` - Basic health status
- `/actuator/health/readiness` - Readiness probe
- `/actuator/health/liveness` - Liveness probe

## Monitoring
- Metrics are available at `/actuator/prometheus` when Prometheus is enabled
- Gateway-specific metrics are available at `/actuator/gateway`
- Distributed tracing with Zipkin integration
- Comprehensive logging with structured format

## Rate Limiting
The gateway implements Redis-based rate limiting with configurable limits per endpoint and user. Default configuration allows 10 requests per second with a burst capacity of 20.

## Circuit Breaker
Uses Resilience4j to implement circuit breaker pattern, preventing cascade failures when downstream services are unavailable.