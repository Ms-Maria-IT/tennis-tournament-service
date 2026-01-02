package com.tennistournament.client;

import com.tennistournament.client.dto.ClubResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Fallback implementation for ClubServiceClient
 * Used when club service is unavailable (circuit breaker pattern)
 */
@Component
public class ClubServiceClientFallback implements ClubServiceClient {

    @Override
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        // Return empty list when service is unavailable
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Override
    public ResponseEntity<ClubResponse> getClubById(Long id) {
        // Return 503 status when service is unavailable
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
