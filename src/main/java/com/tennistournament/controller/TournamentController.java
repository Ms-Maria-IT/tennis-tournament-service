package com.tennistournament.controller;

import com.tennistournament.dto.TournamentRequest;
import com.tennistournament.dto.TournamentResponse;
import com.tennistournament.service.TournamentService;
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
@RequestMapping("/api")
@Tag(name = "Tournament Management", description = "API endpoints for managing tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/clubs/{clubId}/tournaments")
    @Operation(summary = "Create a new tournament", description = "Creates a new tournament for a specific tennis club")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tournament created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Tennis club not found")
    })
    public ResponseEntity<TournamentResponse> createTournament(@PathVariable Long clubId, 
                                                                @Valid @RequestBody TournamentRequest request) {
        TournamentResponse response = tournamentService.createTournament(clubId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tournaments")
    @Operation(summary = "Get all tournaments", description = "Retrieves a list of all tournaments, optionally filtered by club ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tournaments")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments(@RequestParam(required = false) Long clubId) {
        List<TournamentResponse> tournaments = tournamentService.getAllTournaments(clubId);
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/tournaments/{id}")
    @Operation(summary = "Get tournament by ID", description = "Retrieves a specific tournament by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tournament found"),
        @ApiResponse(responseCode = "404", description = "Tournament not found")
    })
    public ResponseEntity<TournamentResponse> getTournamentById(@PathVariable Long id) {
        TournamentResponse response = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tournaments/{tournamentId}/register/{userId}")
    @Operation(summary = "Register user for tournament", description = "Registers a user for a specific tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered for tournament"),
        @ApiResponse(responseCode = "404", description = "Tournament or user not found"),
        @ApiResponse(responseCode = "409", description = "Tournament is full or user already registered")
    })
    public ResponseEntity<Void> registerUserForTournament(@PathVariable Long tournamentId, 
                                                          @PathVariable Long userId) {
        tournamentService.registerUserForTournament(tournamentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tournaments/{tournamentId}/register/{userId}")
    @Operation(summary = "Unregister user from tournament", description = "Unregisters a user from a specific tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully unregistered from tournament"),
        @ApiResponse(responseCode = "404", description = "Tournament, user not found, or user not registered")
    })
    public ResponseEntity<Void> unregisterUserFromTournament(@PathVariable Long tournamentId, 
                                                              @PathVariable Long userId) {
        tournamentService.unregisterUserFromTournament(tournamentId, userId);
        return ResponseEntity.ok().build();
    }
}

