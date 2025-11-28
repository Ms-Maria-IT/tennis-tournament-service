package com.tennistournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Training session name is required")
    @Size(min = 1, max = 100, message = "Training session name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Start date and time is required")
    @Column(nullable = false, name = "start_date_time")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @Column(nullable = false, name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @Size(max = 100, message = "Coach name must not exceed 100 characters")
    @Column(name = "coach_name")
    private String coachName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tennis_club_id", nullable = false)
    private TennisClub tennisClub;

    @ManyToMany
    @JoinTable(
        name = "training_session_attendees",
        joinColumns = @JoinColumn(name = "training_session_id"),
        inverseJoinColumns = @JoinColumn(name = "user_profile_id")
    )
    private Set<UserProfile> attendees = new HashSet<>();

    // Constructors
    public TrainingSession() {
    }

    public TrainingSession(String name, String description, LocalDateTime startDateTime, 
                          LocalDateTime endDateTime, Integer maxAttendees, String coachName, 
                          TennisClub tennisClub) {
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.maxAttendees = maxAttendees;
        this.coachName = coachName;
        this.tennisClub = tennisClub;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public TennisClub getTennisClub() {
        return tennisClub;
    }

    public void setTennisClub(TennisClub tennisClub) {
        this.tennisClub = tennisClub;
    }

    public Set<UserProfile> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<UserProfile> attendees) {
        this.attendees = attendees;
    }
}

