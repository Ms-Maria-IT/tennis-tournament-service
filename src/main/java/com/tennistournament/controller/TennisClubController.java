package com.tennistournament.controller;

import com.tennistournament.dto.TennisClubRequest;
import com.tennistournament.dto.TennisClubResponse;
import com.tennistournament.service.TennisClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@Tag(name = "Tennis Club Management", description = "API endpoints for managing tennis clubs")
public class TennisClubController {

    private final TennisClubService tennisClubService;

    public TennisClubController(TennisClubService tennisClubService) {
        this.tennisClubService = tennisClubService;
    }

    @PostMapping
    @Operation(summary = "Create a new tennis club", description = "Creates a new tennis club with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tennis club created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TennisClubResponse> createClub(@Valid @RequestBody TennisClubRequest request) {
        TennisClubResponse response = tennisClubService.createClub(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all tennis clubs", description = "Retrieves a list of all tennis clubs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tennis clubs")
    public ResponseEntity<List<TennisClubResponse>> getAllClubs() {
        List<TennisClubResponse> clubs = tennisClubService.getAllClubs();
        return ResponseEntity.ok(clubs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tennis club by ID", description = "Retrieves a specific tennis club by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tennis club found"),
        @ApiResponse(responseCode = "404", description = "Tennis club not found")
    })
    public ResponseEntity<TennisClubResponse> getClubById(@PathVariable Long id) {
        TennisClubResponse response = tennisClubService.getClubById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tennis club", description = "Updates an existing tennis club with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tennis club updated successfully"),
        @ApiResponse(responseCode = "404", description = "Tennis club not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TennisClubResponse> updateClub(@PathVariable Long id, 
                                                          @Valid @RequestBody TennisClubRequest request) {
        TennisClubResponse response = tennisClubService.updateClub(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tennis club", description = "Deletes a tennis club by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tennis club deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Tennis club not found")
    })
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        tennisClubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}

