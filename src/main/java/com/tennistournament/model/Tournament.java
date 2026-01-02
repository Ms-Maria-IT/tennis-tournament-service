package com.tennistournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tournament name is required")
    @Size(min = 1, max = 100, message = "Tournament name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Start date and time is required")
    @Column(nullable = false, name = "start_date_time")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @Column(nullable = false, name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @NotNull(message = "Tennis club ID is required")
    @Column(name = "tennis_club_id", nullable = false)
    private Long tennisClubId;

    @ManyToMany
    @JoinTable(
        name = "tournament_participants",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "user_profile_id")
    )
    private Set<UserProfile> participants = new HashSet<>();

    // Constructors
    public Tournament() {
    }

    public Tournament(String name, LocalDateTime startDateTime, LocalDateTime endDateTime, 
                     Integer maxParticipants, Long tennisClubId) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.maxParticipants = maxParticipants;
        this.tennisClubId = tennisClubId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Long getTennisClubId() {
        return tennisClubId;
    }

    public void setTennisClubId(Long tennisClubId) {
        this.tennisClubId = tennisClubId;
    }

    public Set<UserProfile> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserProfile> participants) {
        this.participants = participants;
    }
}

