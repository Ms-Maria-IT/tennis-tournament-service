package com.tennistournament.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainingSessionResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer maxAttendees;
    private String coachName;
    private Long tennisClubId;
    private String tennisClubName;
    private List<Long> attendeeIds = new ArrayList<>();
    private Integer currentAttendeeCount;

    // Constructors
    public TrainingSessionResponse() {
    }

    public TrainingSessionResponse(Long id, String name, String description, LocalDateTime startDateTime, 
                                  LocalDateTime endDateTime, Integer maxAttendees, String coachName, Long tennisClubId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.maxAttendees = maxAttendees;
        this.coachName = coachName;
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

    public Long getTennisClubId() {
        return tennisClubId;
    }

    public void setTennisClubId(Long tennisClubId) {
        this.tennisClubId = tennisClubId;
    }

    public String getTennisClubName() {
        return tennisClubName;
    }

    public void setTennisClubName(String tennisClubName) {
        this.tennisClubName = tennisClubName;
    }

    public List<Long> getAttendeeIds() {
        return attendeeIds;
    }

    public void setAttendeeIds(List<Long> attendeeIds) {
        this.attendeeIds = attendeeIds;
    }

    public Integer getCurrentAttendeeCount() {
        return currentAttendeeCount;
    }

    public void setCurrentAttendeeCount(Integer currentAttendeeCount) {
        this.currentAttendeeCount = currentAttendeeCount;
    }
}

