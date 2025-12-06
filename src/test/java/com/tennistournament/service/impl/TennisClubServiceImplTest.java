package com.tennistournament.service.impl;

import com.tennistournament.dto.TennisClubRequest;
import com.tennistournament.dto.TennisClubResponse;
import com.tennistournament.model.TennisClub;
import com.tennistournament.repository.TennisClubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TennisClubService Unit Tests")
class TennisClubServiceImplTest {

    @Mock
    private TennisClubRepository tennisClubRepository;

    @InjectMocks
    private TennisClubServiceImpl tennisClubService;

    private TennisClubRequest validRequest;
    private TennisClub savedClub;
    private Long clubId;

    @BeforeEach
    void setUp() {
        clubId = 1L;
        validRequest = new TennisClubRequest("Summer Tennis Club", "123 Main Street");
        
        savedClub = new TennisClub();
        savedClub.setId(clubId);
        savedClub.setName("Summer Tennis Club");
        savedClub.setAddress("123 Main Street");
        savedClub.setCourts(new ArrayList<>());
        savedClub.setTournaments(new ArrayList<>());
        savedClub.setTrainingSessions(new ArrayList<>());
    }

    @Test
    @DisplayName("Should create club when valid data provided")
    void shouldCreateClub_WhenValidData() {
        // Arrange
        when(tennisClubRepository.save(any(TennisClub.class))).thenReturn(savedClub);

        // Act
        TennisClubResponse result = tennisClubService.createClub(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clubId);
        assertThat(result.getName()).isEqualTo("Summer Tennis Club");
        assertThat(result.getAddress()).isEqualTo("123 Main Street");
        assertThat(result.getCourtIds()).isEmpty();
        assertThat(result.getTournamentIds()).isEmpty();
        assertThat(result.getTrainingSessionIds()).isEmpty();
        
        verify(tennisClubRepository, times(1)).save(any(TennisClub.class));
    }

    @Test
    @DisplayName("Should create club with null address when address not provided")
    void shouldCreateClub_WhenAddressIsNull() {
        // Arrange
        TennisClubRequest requestWithoutAddress = new TennisClubRequest("Club Name", null);
        savedClub.setAddress(null);
        when(tennisClubRepository.save(any(TennisClub.class))).thenReturn(savedClub);

        // Act
        TennisClubResponse result = tennisClubService.createClub(requestWithoutAddress);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Summer Tennis Club");
        assertThat(result.getAddress()).isNull();
        
        verify(tennisClubRepository, times(1)).save(any(TennisClub.class));
    }

    @Test
    @DisplayName("Should return all clubs when clubs exist")
    void shouldGetAllClubs_WhenClubsExist() {
        // Arrange
        TennisClub club1 = new TennisClub();
        club1.setId(1L);
        club1.setName("Club 1");
        club1.setAddress("Address 1");
        club1.setCourts(new ArrayList<>());
        club1.setTournaments(new ArrayList<>());
        club1.setTrainingSessions(new ArrayList<>());

        TennisClub club2 = new TennisClub();
        club2.setId(2L);
        club2.setName("Club 2");
        club2.setAddress("Address 2");
        club2.setCourts(new ArrayList<>());
        club2.setTournaments(new ArrayList<>());
        club2.setTrainingSessions(new ArrayList<>());

        List<TennisClub> clubs = Arrays.asList(club1, club2);
        when(tennisClubRepository.findAll()).thenReturn(clubs);

        // Act
        List<TennisClubResponse> result = tennisClubService.getAllClubs();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Club 1");
        assertThat(result.get(1).getName()).isEqualTo("Club 2");
        
        verify(tennisClubRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clubs exist")
    void shouldGetAllClubs_WhenNoClubsExist() {
        // Arrange
        when(tennisClubRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<TennisClubResponse> result = tennisClubService.getAllClubs();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(tennisClubRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return club when valid id provided")
    void shouldGetClubById_WhenValidId() {
        // Arrange
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(savedClub));

        // Act
        TennisClubResponse result = tennisClubService.getClubById(clubId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clubId);
        assertThat(result.getName()).isEqualTo("Summer Tennis Club");
        assertThat(result.getAddress()).isEqualTo("123 Main Street");
        
        verify(tennisClubRepository, times(1)).findById(clubId);
    }

    @Test
    @DisplayName("Should throw exception when club not found by id")
    void shouldThrowException_WhenClubNotFoundById() {
        // Arrange
        Long nonExistentId = 999L;
        when(tennisClubRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tennisClubService.getClubById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tennis club not found with id: " + nonExistentId);
                });
        
        verify(tennisClubRepository, times(1)).findById(nonExistentId);
        verify(tennisClubRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update club when valid data and id provided")
    void shouldUpdateClub_WhenValidDataAndId() {
        // Arrange
        TennisClubRequest updateRequest = new TennisClubRequest("Updated Club Name", "Updated Address");
        TennisClub existingClub = new TennisClub();
        existingClub.setId(clubId);
        existingClub.setName("Original Name");
        existingClub.setAddress("Original Address");
        existingClub.setCourts(new ArrayList<>());
        existingClub.setTournaments(new ArrayList<>());
        existingClub.setTrainingSessions(new ArrayList<>());

        TennisClub updatedClub = new TennisClub();
        updatedClub.setId(clubId);
        updatedClub.setName("Updated Club Name");
        updatedClub.setAddress("Updated Address");
        updatedClub.setCourts(new ArrayList<>());
        updatedClub.setTournaments(new ArrayList<>());
        updatedClub.setTrainingSessions(new ArrayList<>());

        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(existingClub));
        when(tennisClubRepository.save(any(TennisClub.class))).thenReturn(updatedClub);

        // Act
        TennisClubResponse result = tennisClubService.updateClub(clubId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clubId);
        assertThat(result.getName()).isEqualTo("Updated Club Name");
        assertThat(result.getAddress()).isEqualTo("Updated Address");
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(tennisClubRepository, times(1)).save(any(TennisClub.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent club")
    void shouldThrowException_WhenUpdatingNonExistentClub() {
        // Arrange
        Long nonExistentId = 999L;
        TennisClubRequest updateRequest = new TennisClubRequest("Updated Name", "Updated Address");
        when(tennisClubRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tennisClubService.updateClub(nonExistentId, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tennis club not found with id: " + nonExistentId);
                });
        
        verify(tennisClubRepository, times(1)).findById(nonExistentId);
        verify(tennisClubRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete club when valid id provided")
    void shouldDeleteClub_WhenValidId() {
        // Arrange
        when(tennisClubRepository.existsById(clubId)).thenReturn(true);
        doNothing().when(tennisClubRepository).deleteById(clubId);

        // Act
        tennisClubService.deleteClub(clubId);

        // Assert
        verify(tennisClubRepository, times(1)).existsById(clubId);
        verify(tennisClubRepository, times(1)).deleteById(clubId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent club")
    void shouldThrowException_WhenDeletingNonExistentClub() {
        // Arrange
        Long nonExistentId = 999L;
        when(tennisClubRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> tennisClubService.deleteClub(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tennis club not found with id: " + nonExistentId);
                });
        
        verify(tennisClubRepository, times(1)).existsById(nonExistentId);
        verify(tennisClubRepository, never()).deleteById(any());
    }
}


