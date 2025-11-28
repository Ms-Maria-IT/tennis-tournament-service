package com.tennistournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Court number is required")
    @Column(nullable = false, name = "court_number")
    private Integer courtNumber;

    @Size(max = 50, message = "Surface type must not exceed 50 characters")
    @Column(name = "surface_type")
    private String surfaceType; // e.g., "GRASS", "CLAY", "HARD"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tennis_club_id", nullable = false)
    private TennisClub tennisClub;

    // Constructors
    public Court() {
    }

    public Court(Integer courtNumber, String surfaceType, TennisClub tennisClub) {
        this.courtNumber = courtNumber;
        this.surfaceType = surfaceType;
        this.tennisClub = tennisClub;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(Integer courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public TennisClub getTennisClub() {
        return tennisClub;
    }

    public void setTennisClub(TennisClub tennisClub) {
        this.tennisClub = tennisClub;
    }
}

