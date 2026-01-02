package com.tennistournament.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Feign client configuration
 */
@Configuration
public class FeignConfig {

    /**
     * Configure Feign logging level
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Configure retry behavior
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }

    /**
     * Configure request timeout
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 10000);
    }

    /**
     * Configure custom error decoder to preserve HTTP status codes from microservices
     * This ensures 404 from club service becomes 404 in monolith, not 500
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
