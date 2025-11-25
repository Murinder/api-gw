package ru.uni.ecop.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregationService {

    private final WebClient.Builder webClientBuilder;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * Aggregate user profile data from multiple services
     */
    public Mono<Map<String, Object>> aggregateUserProfile(String userId) {
        // This is a simplified example - in real implementation, 
        // we would call multiple services and combine their responses
        return Mono.fromCallable(() -> {
            Map<String, Object> aggregatedData = new ConcurrentHashMap<>();
            aggregatedData.put("userId", userId);
            aggregatedData.put("basicInfo", "fetched from core service");
            aggregatedData.put("projects", "fetched from project service");
            aggregatedData.put("events", "fetched from events service");
            aggregatedData.put("portfolio", "fetched from portfolio service");
            return aggregatedData;
        }).doOnError(error -> log.error("Error aggregating user profile data", error));
    }

    /**
     * Aggregate dashboard data from multiple services
     */
    public Mono<Map<String, Object>> aggregateDashboardData(String userId) {
        return Mono.fromCallable(() -> {
            Map<String, Object> aggregatedData = new ConcurrentHashMap<>();
            aggregatedData.put("userId", userId);
            aggregatedData.put("coreData", "fetched from core service");
            aggregatedData.put("projectData", "fetched from project service");
            aggregatedData.put("eventData", "fetched from events service");
            aggregatedData.put("analyticsData", "fetched from analytics service");
            return aggregatedData;
        }).doOnError(error -> log.error("Error aggregating dashboard data", error));
    }

    /**
     * Aggregate search results from multiple services
     */
    public Mono<Map<String, Object>> aggregateSearchResults(String query) {
        return Mono.fromCallable(() -> {
            Map<String, Object> aggregatedData = new ConcurrentHashMap<>();
            aggregatedData.put("query", query);
            aggregatedData.put("users", "fetched from core service");
            aggregatedData.put("projects", "fetched from project service");
            aggregatedData.put("events", "fetched from events service");
            aggregatedData.put("portfolios", "fetched from portfolio service");
            return aggregatedData;
        }).doOnError(error -> log.error("Error aggregating search results", error));
    }
}