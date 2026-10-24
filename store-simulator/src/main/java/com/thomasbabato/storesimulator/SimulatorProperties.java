
package com.thomasbabato.storesimulator;

import org.springframework.boot.context.properties.ConfigurationProperties;


 @ConfigurationProperties(prefix = "simulator")
 public record SimulatorProperties(
    int stores,
    int products,
    long intervalMs
 ) {}