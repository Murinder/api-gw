package ru.uni.ecop.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.uni.ecop.gateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;

@Configuration
public class GatewayRoutesConfig {
    
    private final AuthenticationFilter authenticationFilter;
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver userKeyResolver;
    
    public GatewayRoutesConfig(AuthenticationFilter authenticationFilter, 
                              RedisRateLimiter redisRateLimiter,
                              KeyResolver userKeyResolver) {
        this.authenticationFilter = authenticationFilter;
        this.redisRateLimiter = redisRateLimiter;
        this.userKeyResolver = userKeyResolver;
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Core Service Authentication and Authorization endpoints
            .route("auth_login_route", r -> r.path("/api/auth/login")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("auth_register_route", r -> r.path("/api/auth/register")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("auth_verify_route", r -> r.path("/api/auth/verify")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("auth_reset_password_route", r -> r.path("/api/auth/reset-password")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("auth_refresh_token_route", r -> r.path("/api/auth/refresh-token")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("auth_validate_route", r -> r.path("/api/auth/validate")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Core Service User management endpoints
            .route("user_profile_route", r -> r.path("/api/users/profile")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("user_get_route", r -> r.path("/api/users/{id}")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("user_search_route", r -> r.path("/api/users/search")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("user_skills_route", r -> r.path("/api/users/skills")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            .route("user_connections_route", r -> r.path("/api/users/connections")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Core Service Dashboard endpoints
            .route("dashboard_route", r -> r.path("/api/dashboards/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Core Service Notification endpoints
            .route("notification_route", r -> r.path("/api/notifications/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Core Service Search endpoints
            .route("search_route", r -> r.path("/api/search/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Core Service Communication endpoints
            .route("chat_route", r -> r.path("/api/chats/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://core-service"))
            
            // Project Management Service routes
            .route("project_route", r -> r.path("/api/projects/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://project-service"))
            
            // Events Service routes
            .route("event_route", r -> r.path("/api/events/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://events-service"))
            
            // Portfolio Service routes
            .route("portfolio_route", r -> r.path("/api/portfolios/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://portfolio-service"))
            
            // Analytics Service routes
            .route("analytics_route", r -> r.path("/api/analytics/**")
                .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                    .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://analytics-service"))
            
            .build();
    }
}