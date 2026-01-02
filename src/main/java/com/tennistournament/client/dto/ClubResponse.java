package com.tennistournament.client.dto;

import java.util.List;

/**
 * DTO for club service responses
 * This matches the response structure from tennis-club-service
 */
public class ClubResponse {
    private Long id;
    private String name;
    private String address;
    private List<Long> courtIds;

    // Constructors
    public ClubResponse() {
    }

    public ClubResponse(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Long> getCourtIds() {
        return courtIds;
    }

    public void setCourtIds(List<Long> courtIds) {
        this.courtIds = courtIds;
    }
}
