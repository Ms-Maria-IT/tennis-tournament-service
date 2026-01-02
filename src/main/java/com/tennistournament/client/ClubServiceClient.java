package com.tennistournament.client;

import com.tennistournament.client.dto.ClubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communicating with tennis-club-service microservice
 * Service URL: http://localhost:8081
 */
@FeignClient(
    name = "tennis-club-service",
    url = "${club.service.url:http://localhost:8081}",
    fallback = ClubServiceClientFallback.class
)
public interface ClubServiceClient {

    /**
     * Get all clubs
     */
    @GetMapping("/api/clubs")
    ResponseEntity<List<ClubResponse>> getAllClubs();

    /**
     * Get club by ID
     */
    @GetMapping("/api/clubs/{id}")
    ResponseEntity<ClubResponse> getClubById(@PathVariable Long id);
}
