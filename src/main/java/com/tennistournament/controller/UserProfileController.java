package com.tennistournament.controller;

import com.tennistournament.dto.UserProfileRequest;
import com.tennistournament.dto.UserProfileResponse;
import com.tennistournament.service.UserProfileService;
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
@RequestMapping("/api/users")
@Tag(name = "User Profile Management", description = "API endpoints for managing user profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    @Operation(summary = "Create a new user profile", description = "Creates a new user profile with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<UserProfileResponse> createUser(@Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all user profiles", description = "Retrieves a list of all user profiles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user profiles")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userProfileService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID", description = "Retrieves a specific user profile by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile found"),
        @ApiResponse(responseCode = "404", description = "User profile not found")
    })
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        UserProfileResponse response = userProfileService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates an existing user profile with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "User profile not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<UserProfileResponse> updateUser(@PathVariable Long id, 
                                                          @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
}

