package ru.uni.ecop.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    private final WebClient.Builder webClientBuilder;
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/verify",
        "/api/auth/reset-password"
    );
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            // Skip authentication for public endpoints
            if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }
            
            // Check for authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }
            
            String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            return validateToken(exchange, chain, token);
        };
    }
    
    private Mono<Void> validateToken(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        return webClientBuilder.build()
            .get()
            .uri("http://core-service/api/auth/validate")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(Void.class)
            .onErrorResume(e -> Mono.empty()) // Continue on error to let core service handle it
            .flatMap(aVoid -> chain.filter(exchange))
            .onErrorResume(e -> onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED));
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        log.error("Error in authentication filter: {}", errorMessage);
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        
        ErrorResponse errorResponse = new ErrorResponse(
            httpStatus.value(),
            httpStatus.getReasonPhrase(),
            errorMessage
        );
        
        byte[] bytes = errorResponse.toString().getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        return response.writeWith(Mono.just(buffer));
    }
    
    public static class Config {
        // Configuration parameters if needed
    }
}