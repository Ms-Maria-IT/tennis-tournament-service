package com.tennistournament.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentResponse {
    private Long id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer maxParticipants;
    private Long tennisClubId;
    private String tennisClubName;
    private List<Long> participantIds = new ArrayList<>();
    private Integer currentParticipantCount;

    // Constructors
    public TournamentResponse() {
    }

    public TournamentResponse(Long id, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, 
                             Integer maxParticipants, Long tennisClubId) {
        this.id = id;
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

    public String getTennisClubName() {
        return tennisClubName;
    }

    public void setTennisClubName(String tennisClubName) {
        this.tennisClubName = tennisClubName;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public Integer getCurrentParticipantCount() {
        return currentParticipantCount;
    }

    public void setCurrentParticipantCount(Integer currentParticipantCount) {
        this.currentParticipantCount = currentParticipantCount;
    }
}

