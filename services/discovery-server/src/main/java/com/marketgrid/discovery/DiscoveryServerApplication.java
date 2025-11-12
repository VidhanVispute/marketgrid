package com.marketgrid.discovery;  // Root pkg: com.marketgrid.discovery – Why? Service-specific sub-pkg under root groupId.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;  // Enables registry

@SpringBootApplication  // Auto-config: Scans @Beans, enables web if needed (but Eureka is server-only)
@EnableEurekaServer     // Registers this as Eureka; peers can join cluster later
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);  // Starts on 8761
    }
}