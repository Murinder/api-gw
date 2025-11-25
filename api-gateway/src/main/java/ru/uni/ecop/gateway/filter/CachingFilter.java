package ru.uni.ecop.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CachingFilter extends AbstractGatewayFilterFactory<CachingFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ConcurrentHashMap<String, CachedResponse> localCache = new ConcurrentHashMap<>();

    public CachingFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String cacheKey = generateCacheKey(request);

            // Try to get from local cache first
            CachedResponse cachedResponse = localCache.get(cacheKey);
            if (cachedResponse != null && !cachedResponse.isExpired()) {
                log.debug("Serving response from local cache for key: {}", cacheKey);
                // Return cached response
                exchange.getResponse().getHeaders().addAll(cachedResponse.getHeaders());
                return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory()
                        .wrap(cachedResponse.getBody().getBytes()))
                );
            }

            // Try to get from Redis cache
            return redisTemplate.opsForValue().get(cacheKey)
                .cast(String.class)
                .flatMap(cachedData -> {
                    log.debug("Serving response from Redis cache for key: {}", cacheKey);
                    // In a real implementation, we would parse the cached data and set response
                    return exchange.getResponse().setComplete();
                })
                .switchIfEmpty(chain.filter(exchange).doOnSuccess(response -> {
                    // Cache the response if it's a successful GET request
                    if (request.getMethod().name().equals("GET") && 
                        response.getStatusCode().is2xxSuccessful()) {
                        cacheResponse(cacheKey, response, config.getTtl());
                    }
                }));
        };
    }

    private String generateCacheKey(ServerHttpRequest request) {
        return request.getMethod().name() + ":" + request.getURI().toString();
    }

    private void cacheResponse(String cacheKey, org.springframework.web.server.ServerHttpResponse response, Duration ttl) {
        // In a real implementation, we would capture the response body and cache it
        // For now, we just cache the key with TTL
        CachedResponse cachedResponse = new CachedResponse(
            response.getHeaders(),
            "cached_response_placeholder",
            System.currentTimeMillis() + ttl.toMillis()
        );
        
        localCache.put(cacheKey, cachedResponse);
        
        // Also cache in Redis
        redisTemplate.opsForValue()
            .set(cacheKey, "cached_response_placeholder", ttl)
            .subscribe();
    }

    public static class Config {
        private Duration ttl = Duration.ofMinutes(5); // Default TTL: 5 minutes
        private boolean enabled = true;

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private static class CachedResponse {
        private final org.springframework.http.HttpHeaders headers;
        private final String body;
        private final long expiryTime;

        public CachedResponse(org.springframework.http.HttpHeaders headers, String body, long expiryTime) {
            this.headers = headers;
            this.body = body;
            this.expiryTime = expiryTime;
        }

        public org.springframework.http.HttpHeaders getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}