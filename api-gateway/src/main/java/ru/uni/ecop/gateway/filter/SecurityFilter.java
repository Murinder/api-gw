package ru.uni.ecop.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SecurityFilter extends AbstractGatewayFilterFactory<SecurityFilter.Config> {

    // Patterns for SQL injection detection
    private static final List<Pattern> SQL_INJECTION_PATTERNS = List.of(
        Pattern.compile(".*([';])+.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*((union(\\s+)(all|select|distinct)?|having|group(\\s+)by|order(\\s+)by|insert|update|delete|create|drop|truncate|rename|alter)|exec(\\s)+(s|x)p(\\s)+).*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*cast\\(.*as.*).*", Pattern.CASE_INSENSITIVE)
    );

    // Patterns for XSS detection
    private static final List<Pattern> XSS_PATTERNS = List.of(
        Pattern.compile(".*<script.*>.*</script>.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*javascript:.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*onload=.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*onerror=.*", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Check for malicious patterns in query parameters and headers
            boolean isMalicious = isRequestMalicious(request);
            
            if (isMalicious) {
                log.warn("Malicious request detected: {}", request.getURI());
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            
            return chain.filter(exchange);
        };
    }

    private boolean isRequestMalicious(ServerHttpRequest request) {
        // Check query parameters for SQL injection or XSS
        for (var param : request.getQueryParams().entrySet()) {
            for (String value : param.getValue()) {
                if (isMaliciousContent(value)) {
                    return true;
                }
            }
        }

        // Check headers for malicious content
        for (var header : request.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                if (isMaliciousContent(value)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isMaliciousContent(String content) {
        if (content == null) {
            return false;
        }

        // Check for SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(content).matches()) {
                log.debug("SQL injection pattern detected: {}", content);
                return true;
            }
        }

        // Check for XSS patterns
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(content).matches()) {
                log.debug("XSS pattern detected: {}", content);
                return true;
            }
        }

        return false;
    }

    public static class Config {
        // Configuration parameters if needed
    }
}