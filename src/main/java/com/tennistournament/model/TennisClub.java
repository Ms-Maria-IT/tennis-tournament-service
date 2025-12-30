package com.tennistournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TODO: TEMPORARY STUB - TennisClub functionality has been extracted to tennis-club-service microservice
 * TODO: This entity should be removed once Tournament and TrainingSession are updated to use clubId (Long) instead
 * TODO: and REST client integration is implemented to call tennis-club-service
 * 
 * This is kept temporarily to maintain compilation until REST client integration is complete.
 * Tournament and TrainingSession services should call http://localhost:8081/api/clubs/{clubId} instead.
 */
@Entity
@Table(name = "tennis_clubs")
@Deprecated
public class TennisClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Club name is required")
    @Size(min = 1, max = 100, message = "Club name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    // Constructors
    public TennisClub() {
    }

    public TennisClub(String name, String address) {
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
}
