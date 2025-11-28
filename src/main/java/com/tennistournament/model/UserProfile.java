package com.tennistournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 20, message = "Skill level must not exceed 20 characters")
    @Column(name = "skill_level")
    private String skillLevel; // e.g., "BEGINNER", "INTERMEDIATE", "ADVANCED"

    @ManyToMany(mappedBy = "participants")
    private Set<Tournament> registeredTournaments = new HashSet<>();

    @ManyToMany(mappedBy = "attendees")
    private Set<TrainingSession> registeredTrainingSessions = new HashSet<>();

    // Constructors
    public UserProfile() {
    }

    public UserProfile(String username, String email, String firstName, String lastName, String skillLevel) {
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

    public Set<Tournament> getRegisteredTournaments() {
        return registeredTournaments;
    }

    public void setRegisteredTournaments(Set<Tournament> registeredTournaments) {
        this.registeredTournaments = registeredTournaments;
    }

    public Set<TrainingSession> getRegisteredTrainingSessions() {
        return registeredTrainingSessions;
    }

    public void setRegisteredTrainingSessions(Set<TrainingSession> registeredTrainingSessions) {
        this.registeredTrainingSessions = registeredTrainingSessions;
    }
}

