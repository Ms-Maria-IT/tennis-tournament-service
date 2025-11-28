package com.tennistournament.controller;

import com.tennistournament.dto.TrainingSessionRequest;
import com.tennistournament.dto.TrainingSessionResponse;
import com.tennistournament.service.TrainingSessionService;
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
@Tag(name = "Training Session Management", description = "API endpoints for managing training sessions")
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    @PostMapping("/clubs/{clubId}/trainings")
    @Operation(summary = "Create a new training session", description = "Creates a new training session for a specific tennis club")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Training session created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Tennis club not found")
    })
    public ResponseEntity<TrainingSessionResponse> createTrainingSession(@PathVariable Long clubId, 
                                                                          @Valid @RequestBody TrainingSessionRequest request) {
        TrainingSessionResponse response = trainingSessionService.createTrainingSession(clubId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/trainings")
    @Operation(summary = "Get all training sessions", description = "Retrieves a list of all training sessions, optionally filtered by club ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of training sessions")
    public ResponseEntity<List<TrainingSessionResponse>> getAllTrainingSessions(@RequestParam(required = false) Long clubId) {
        List<TrainingSessionResponse> sessions = trainingSessionService.getAllTrainingSessions(clubId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/trainings/{id}")
    @Operation(summary = "Get training session by ID", description = "Retrieves a specific training session by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Training session found"),
        @ApiResponse(responseCode = "404", description = "Training session not found")
    })
    public ResponseEntity<TrainingSessionResponse> getTrainingSessionById(@PathVariable Long id) {
        TrainingSessionResponse response = trainingSessionService.getTrainingSessionById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trainings/{sessionId}/register/{userId}")
    @Operation(summary = "Register user for training session", description = "Registers a user for a specific training session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered for training session"),
        @ApiResponse(responseCode = "404", description = "Training session or user not found"),
        @ApiResponse(responseCode = "409", description = "Training session is full or user already registered")
    })
    public ResponseEntity<Void> registerUserForTrainingSession(@PathVariable Long sessionId, 
                                                               @PathVariable Long userId) {
        trainingSessionService.registerUserForTrainingSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/trainings/{sessionId}/register/{userId}")
    @Operation(summary = "Unregister user from training session", description = "Unregisters a user from a specific training session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully unregistered from training session"),
        @ApiResponse(responseCode = "404", description = "Training session, user not found, or user not registered")
    })
    public ResponseEntity<Void> unregisterUserFromTrainingSession(@PathVariable Long sessionId, 
                                                                  @PathVariable Long userId) {
        trainingSessionService.unregisterUserFromTrainingSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }
}

