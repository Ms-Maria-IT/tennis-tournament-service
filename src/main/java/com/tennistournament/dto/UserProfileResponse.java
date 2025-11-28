package com.tennistournament.dto;

import java.util.ArrayList;
import java.util.List;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String skillLevel;
    private List<Long> registeredTournamentIds = new ArrayList<>();
    private List<Long> registeredTrainingSessionIds = new ArrayList<>();

    // Constructors
    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String email, String firstName, String lastName, String skillLevel) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skillLevel = skillLevel;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public List<Long> getRegisteredTournamentIds() {
        return registeredTournamentIds;
    }

    public void setRegisteredTournamentIds(List<Long> registeredTournamentIds) {
        this.registeredTournamentIds = registeredTournamentIds;
    }

    public List<Long> getRegisteredTrainingSessionIds() {
        return registeredTrainingSessionIds;
    }

    public void setRegisteredTrainingSessionIds(List<Long> registeredTrainingSessionIds) {
        this.registeredTrainingSessionIds = registeredTrainingSessionIds;
    }
}

