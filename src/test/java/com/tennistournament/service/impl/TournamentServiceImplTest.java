package com.tennistournament.service.impl;

import com.tennistournament.dto.TournamentRequest;
import com.tennistournament.dto.TournamentResponse;
import com.tennistournament.model.TennisClub;
import com.tennistournament.model.Tournament;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.TennisClubRepository;
import com.tennistournament.repository.TournamentRepository;
import com.tennistournament.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TournamentService Unit Tests")
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TennisClubRepository tennisClubRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Long clubId;
    private Long tournamentId;
    private Long userId;
    private TennisClub tennisClub;
    private Tournament tournament;
    private UserProfile user;
    private TournamentRequest validRequest;

    @BeforeEach
    void setUp() {
        clubId = 1L;
        tournamentId = 1L;
        userId = 1L;

        tennisClub = new TennisClub();
        tennisClub.setId(clubId);
        tennisClub.setName("Summer Tennis Club");
        tennisClub.setAddress("123 Main Street");

        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Summer Cup");
        tournament.setStartDateTime(LocalDateTime.now().plusDays(1));
        tournament.setEndDateTime(LocalDateTime.now().plusDays(2));
        tournament.setMaxParticipants(32);
        tournament.setTennisClub(tennisClub);
        tournament.setParticipants(new HashSet<>());

        user = new UserProfile();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        validRequest = new TournamentRequest(
                "Summer Cup",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                32
        );
    }

    @Test
    @DisplayName("Should create tournament when valid data provided")
    void shouldCreateTournament_WhenValidData() {
        // Arrange
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        TournamentResponse result = tournamentService.createTournament(clubId, validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tournamentId);
        assertThat(result.getName()).isEqualTo("Summer Cup");
        assertThat(result.getStartDateTime()).isEqualTo(tournament.getStartDateTime());
        assertThat(result.getEndDateTime()).isEqualTo(tournament.getEndDateTime());
        assertThat(result.getMaxParticipants()).isEqualTo(32);
        assertThat(result.getTennisClubId()).isEqualTo(clubId);
        assertThat(result.getTennisClubName()).isEqualTo("Summer Tennis Club");
        assertThat(result.getCurrentParticipantCount()).isEqualTo(0);
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should throw exception when club not found")
    void shouldThrowException_WhenClubNotFound() {
        // Arrange
        Long nonExistentClubId = 999L;
        when(tennisClubRepository.findById(nonExistentClubId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.createTournament(nonExistentClubId, validRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tennis club not found with id: " + nonExistentClubId);
                });
        
        verify(tennisClubRepository, times(1)).findById(nonExistentClubId);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowException_WhenEndDateBeforeStartDate() {
        // Arrange
        TournamentRequest invalidRequest = new TournamentRequest(
                "Summer Cup",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1), // End before start
                32
        );
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.createTournament(clubId, invalidRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).contains("End date and time must be after start date and time");
                });
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create tournament with null max participants")
    void shouldCreateTournament_WhenMaxParticipantsIsNull() {
        // Arrange
        TournamentRequest requestWithoutMax = new TournamentRequest(
                "Summer Cup",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null
        );
        tournament.setMaxParticipants(null);
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        TournamentResponse result = tournamentService.createTournament(clubId, requestWithoutMax);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMaxParticipants()).isNull();
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should return all tournaments when club id provided")
    void shouldGetAllTournaments_WhenClubIdProvided() {
        // Arrange
        Tournament tournament2 = new Tournament();
        tournament2.setId(2L);
        tournament2.setName("Winter Cup");
        tournament2.setTennisClub(tennisClub);
        tournament2.setParticipants(new HashSet<>());

        List<Tournament> tournaments = Arrays.asList(tournament, tournament2);
        when(tournamentRepository.findByTennisClubId(clubId)).thenReturn(tournaments);

        // Act
        List<TournamentResponse> result = tournamentService.getAllTournaments(clubId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Summer Cup");
        assertThat(result.get(1).getName()).isEqualTo("Winter Cup");
        
        verify(tournamentRepository, times(1)).findByTennisClubId(clubId);
        verify(tournamentRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should return all tournaments when club id is null")
    void shouldGetAllTournaments_WhenClubIdIsNull() {
        // Arrange
        List<Tournament> tournaments = Arrays.asList(tournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        // Act
        List<TournamentResponse> result = tournamentService.getAllTournaments(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        
        verify(tournamentRepository, times(1)).findAll();
        verify(tournamentRepository, never()).findByTennisClubId(any());
    }

    @Test
    @DisplayName("Should return empty list when no tournaments exist")
    void shouldGetAllTournaments_WhenNoTournamentsExist() {
        // Arrange
        when(tournamentRepository.findByTennisClubId(clubId)).thenReturn(new ArrayList<>());

        // Act
        List<TournamentResponse> result = tournamentService.getAllTournaments(clubId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(tournamentRepository, times(1)).findByTennisClubId(clubId);
    }

    @Test
    @DisplayName("Should return tournament when valid id provided")
    void shouldGetTournamentById_WhenValidId() {
        // Arrange
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        TournamentResponse result = tournamentService.getTournamentById(tournamentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tournamentId);
        assertThat(result.getName()).isEqualTo("Summer Cup");
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    @DisplayName("Should throw exception when tournament not found by id")
    void shouldThrowException_WhenTournamentNotFoundById() {
        // Arrange
        Long nonExistentId = 999L;
        when(tournamentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.getTournamentById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tournament not found with id: " + nonExistentId);
                });
        
        verify(tournamentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should register user for tournament when valid data provided")
    void shouldRegisterUserForTournament_WhenValidData() {
        // Arrange
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        tournamentService.registerUserForTournament(tournamentId, userId);

        // Assert
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should throw exception when tournament not found during registration")
    void shouldThrowException_WhenTournamentNotFoundDuringRegistration() {
        // Arrange
        Long nonExistentTournamentId = 999L;
        when(tournamentRepository.findById(nonExistentTournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.registerUserForTournament(nonExistentTournamentId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tournament not found with id: " + nonExistentTournamentId);
                });
        
        verify(tournamentRepository, times(1)).findById(nonExistentTournamentId);
        verify(userProfileRepository, never()).findById(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during registration")
    void shouldThrowException_WhenUserNotFoundDuringRegistration() {
        // Arrange
        Long nonExistentUserId = 999L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.registerUserForTournament(tournamentId, nonExistentUserId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentUserId);
                });
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(nonExistentUserId);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user already registered")
    void shouldThrowException_WhenUserAlreadyRegistered() {
        // Arrange
        tournament.getParticipants().add(user);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.registerUserForTournament(tournamentId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("User is already registered for this tournament");
                });
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {32, 50, 100})
    @DisplayName("Should throw exception when max participants reached")
    void shouldThrowException_WhenMaxParticipantsReached(int maxParticipants) {
        // Arrange
        tournament.setMaxParticipants(maxParticipants);
        for (int i = 0; i < maxParticipants; i++) {
            UserProfile participant = new UserProfile();
            participant.setId((long) (i + 100));
            tournament.getParticipants().add(participant);
        }
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.registerUserForTournament(tournamentId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Tournament has reached maximum number of participants");
                });
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow registration when max participants is null")
    void shouldAllowRegistration_WhenMaxParticipantsIsNull() {
        // Arrange
        tournament.setMaxParticipants(null);
        // Add some participants
        for (int i = 0; i < 50; i++) {
            UserProfile participant = new UserProfile();
            participant.setId((long) (i + 100));
            tournament.getParticipants().add(participant);
        }
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        tournamentService.registerUserForTournament(tournamentId, userId);

        // Assert
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should unregister user from tournament when valid data provided")
    void shouldUnregisterUserFromTournament_WhenValidData() {
        // Arrange
        tournament.getParticipants().add(user);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        tournamentService.unregisterUserFromTournament(tournamentId, userId);

        // Assert
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("Should throw exception when tournament not found during unregistration")
    void shouldThrowException_WhenTournamentNotFoundDuringUnregistration() {
        // Arrange
        Long nonExistentTournamentId = 999L;
        when(tournamentRepository.findById(nonExistentTournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.unregisterUserFromTournament(nonExistentTournamentId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tournament not found with id: " + nonExistentTournamentId);
                });
        
        verify(tournamentRepository, times(1)).findById(nonExistentTournamentId);
        verify(userProfileRepository, never()).findById(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during unregistration")
    void shouldThrowException_WhenUserNotFoundDuringUnregistration() {
        // Arrange
        Long nonExistentUserId = 999L;
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.unregisterUserFromTournament(tournamentId, nonExistentUserId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentUserId);
                });
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(nonExistentUserId);
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not registered for tournament")
    void shouldThrowException_WhenUserNotRegisteredForTournament() {
        // Arrange
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> tournamentService.unregisterUserFromTournament(tournamentId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User is not registered for this tournament");
                });
        
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(tournamentRepository, never()).save(any());
    }
}


