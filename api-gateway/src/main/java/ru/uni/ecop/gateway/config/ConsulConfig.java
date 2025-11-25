package ru.uni.ecop.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class ConsulConfig {

    @Value("${consul.host:consul}")
    private String consulHost;

    @Value("${consul.port:8500}")
    private int consulPort;

    // Additional Consul configuration can be added here
}