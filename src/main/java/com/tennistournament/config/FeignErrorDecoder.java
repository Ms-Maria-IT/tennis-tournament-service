package com.tennistournament.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Custom Feign error decoder to properly handle HTTP status codes from microservices
 * Converts FeignException to ResponseStatusException to preserve status codes
 */
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        
        // For 404 errors, throw ResponseStatusException with 404 status
        if (status == HttpStatus.NOT_FOUND) {
            String message = String.format("Resource not found: %s", methodKey);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        
        // For 4xx client errors, preserve the status code
        if (status.is4xxClientError()) {
            String message = String.format("Client error: %s - %s", status.value(), methodKey);
            return new ResponseStatusException(status, message);
        }
        
        // For 5xx server errors, preserve the status code
        if (status.is5xxServerError()) {
            String message = String.format("Server error: %s - %s", status.value(), methodKey);
            return new ResponseStatusException(status, message);
        }
        
        // For other errors, use default decoder
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
