package com.tennistournament.service.impl;

import com.tennistournament.dto.TrainingSessionRequest;
import com.tennistournament.dto.TrainingSessionResponse;
import com.tennistournament.model.TennisClub;
import com.tennistournament.model.TrainingSession;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.TennisClubRepository;
import com.tennistournament.repository.TrainingSessionRepository;
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
@DisplayName("TrainingSessionService Unit Tests")
class TrainingSessionServiceImplTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private TennisClubRepository tennisClubRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private TrainingSessionServiceImpl trainingSessionService;

    private Long clubId;
    private Long sessionId;
    private Long userId;
    private TennisClub tennisClub;
    private TrainingSession trainingSession;
    private UserProfile user;
    private TrainingSessionRequest validRequest;

    @BeforeEach
    void setUp() {
        clubId = 1L;
        sessionId = 1L;
        userId = 1L;

        tennisClub = new TennisClub();
        tennisClub.setId(clubId);
        tennisClub.setName("Summer Tennis Club");
        tennisClub.setAddress("123 Main Street");

        trainingSession = new TrainingSession();
        trainingSession.setId(sessionId);
        trainingSession.setName("Beginner Training");
        trainingSession.setDescription("Training session for beginners");
        trainingSession.setStartDateTime(LocalDateTime.now().plusDays(1));
        trainingSession.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(2));
        trainingSession.setMaxAttendees(20);
        trainingSession.setCoachName("Coach Smith");
        trainingSession.setTennisClub(tennisClub);
        trainingSession.setAttendees(new HashSet<>());

        user = new UserProfile();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        validRequest = new TrainingSessionRequest(
                "Beginner Training",
                "Training session for beginners",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                20,
                "Coach Smith"
        );
    }

    @Test
    @DisplayName("Should create training session when valid data provided")
    void shouldCreateTrainingSession_WhenValidData() {
        // Arrange
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        TrainingSessionResponse result = trainingSessionService.createTrainingSession(clubId, validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        assertThat(result.getName()).isEqualTo("Beginner Training");
        assertThat(result.getDescription()).isEqualTo("Training session for beginners");
        assertThat(result.getStartDateTime()).isEqualTo(trainingSession.getStartDateTime());
        assertThat(result.getEndDateTime()).isEqualTo(trainingSession.getEndDateTime());
        assertThat(result.getMaxAttendees()).isEqualTo(20);
        assertThat(result.getCoachName()).isEqualTo("Coach Smith");
        assertThat(result.getTennisClubId()).isEqualTo(clubId);
        assertThat(result.getTennisClubName()).isEqualTo("Summer Tennis Club");
        assertThat(result.getCurrentAttendeeCount()).isEqualTo(0);
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when club not found")
    void shouldThrowException_WhenClubNotFound() {
        // Arrange
        Long nonExistentClubId = 999L;
        when(tennisClubRepository.findById(nonExistentClubId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.createTrainingSession(nonExistentClubId, validRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Tennis club not found with id: " + nonExistentClubId);
                });
        
        verify(tennisClubRepository, times(1)).findById(nonExistentClubId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowException_WhenEndDateBeforeStartDate() {
        // Arrange
        TrainingSessionRequest invalidRequest = new TrainingSessionRequest(
                "Beginner Training",
                "Description",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1), // End before start
                20,
                "Coach Smith"
        );
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.createTrainingSession(clubId, invalidRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).contains("End date and time must be after start date and time");
                });
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create training session with null optional fields")
    void shouldCreateTrainingSession_WhenOptionalFieldsAreNull() {
        // Arrange
        TrainingSessionRequest requestWithNulls = new TrainingSessionRequest(
                "Training Session",
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                null,
                null
        );
        trainingSession.setDescription(null);
        trainingSession.setMaxAttendees(null);
        trainingSession.setCoachName(null);
        
        when(tennisClubRepository.findById(clubId)).thenReturn(Optional.of(tennisClub));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        TrainingSessionResponse result = trainingSessionService.createTrainingSession(clubId, requestWithNulls);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Beginner Training");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getMaxAttendees()).isNull();
        assertThat(result.getCoachName()).isNull();
        
        verify(tennisClubRepository, times(1)).findById(clubId);
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Should return all training sessions when club id provided")
    void shouldGetAllTrainingSessions_WhenClubIdProvided() {
        // Arrange
        TrainingSession session2 = new TrainingSession();
        session2.setId(2L);
        session2.setName("Advanced Training");
        session2.setTennisClub(tennisClub);
        session2.setAttendees(new HashSet<>());

        List<TrainingSession> sessions = Arrays.asList(trainingSession, session2);
        when(trainingSessionRepository.findByTennisClubId(clubId)).thenReturn(sessions);

        // Act
        List<TrainingSessionResponse> result = trainingSessionService.getAllTrainingSessions(clubId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Beginner Training");
        assertThat(result.get(1).getName()).isEqualTo("Advanced Training");
        
        verify(trainingSessionRepository, times(1)).findByTennisClubId(clubId);
        verify(trainingSessionRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should return all training sessions when club id is null")
    void shouldGetAllTrainingSessions_WhenClubIdIsNull() {
        // Arrange
        List<TrainingSession> sessions = Arrays.asList(trainingSession);
        when(trainingSessionRepository.findAll()).thenReturn(sessions);

        // Act
        List<TrainingSessionResponse> result = trainingSessionService.getAllTrainingSessions(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        
        verify(trainingSessionRepository, times(1)).findAll();
        verify(trainingSessionRepository, never()).findByTennisClubId(any());
    }

    @Test
    @DisplayName("Should return empty list when no training sessions exist")
    void shouldGetAllTrainingSessions_WhenNoSessionsExist() {
        // Arrange
        when(trainingSessionRepository.findByTennisClubId(clubId)).thenReturn(new ArrayList<>());

        // Act
        List<TrainingSessionResponse> result = trainingSessionService.getAllTrainingSessions(clubId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(trainingSessionRepository, times(1)).findByTennisClubId(clubId);
    }

    @Test
    @DisplayName("Should return training session when valid id provided")
    void shouldGetTrainingSessionById_WhenValidId() {
        // Arrange
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));

        // Act
        TrainingSessionResponse result = trainingSessionService.getTrainingSessionById(sessionId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        assertThat(result.getName()).isEqualTo("Beginner Training");
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("Should throw exception when training session not found by id")
    void shouldThrowException_WhenTrainingSessionNotFoundById() {
        // Arrange
        Long nonExistentId = 999L;
        when(trainingSessionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.getTrainingSessionById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Training session not found with id: " + nonExistentId);
                });
        
        verify(trainingSessionRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should register user for training session when valid data provided")
    void shouldRegisterUserForTrainingSession_WhenValidData() {
        // Arrange
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        trainingSessionService.registerUserForTrainingSession(sessionId, userId);

        // Assert
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when training session not found during registration")
    void shouldThrowException_WhenTrainingSessionNotFoundDuringRegistration() {
        // Arrange
        Long nonExistentSessionId = 999L;
        when(trainingSessionRepository.findById(nonExistentSessionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.registerUserForTrainingSession(nonExistentSessionId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Training session not found with id: " + nonExistentSessionId);
                });
        
        verify(trainingSessionRepository, times(1)).findById(nonExistentSessionId);
        verify(userProfileRepository, never()).findById(any());
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during registration")
    void shouldThrowException_WhenUserNotFoundDuringRegistration() {
        // Arrange
        Long nonExistentUserId = 999L;
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.registerUserForTrainingSession(sessionId, nonExistentUserId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentUserId);
                });
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(nonExistentUserId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user already registered")
    void shouldThrowException_WhenUserAlreadyRegistered() {
        // Arrange
        trainingSession.getAttendees().add(user);
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.registerUserForTrainingSession(sessionId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("User is already registered for this training session");
                });
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {20, 30, 50})
    @DisplayName("Should throw exception when max attendees reached")
    void shouldThrowException_WhenMaxAttendeesReached(int maxAttendees) {
        // Arrange
        trainingSession.setMaxAttendees(maxAttendees);
        for (int i = 0; i < maxAttendees; i++) {
            UserProfile attendee = new UserProfile();
            attendee.setId((long) (i + 100));
            trainingSession.getAttendees().add(attendee);
        }
        
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.registerUserForTrainingSession(sessionId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Training session has reached maximum number of attendees");
                });
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow registration when max attendees is null")
    void shouldAllowRegistration_WhenMaxAttendeesIsNull() {
        // Arrange
        trainingSession.setMaxAttendees(null);
        // Add some attendees
        for (int i = 0; i < 50; i++) {
            UserProfile attendee = new UserProfile();
            attendee.setId((long) (i + 100));
            trainingSession.getAttendees().add(attendee);
        }
        
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        trainingSessionService.registerUserForTrainingSession(sessionId, userId);

        // Assert
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Should unregister user from training session when valid data provided")
    void shouldUnregisterUserFromTrainingSession_WhenValidData() {
        // Arrange
        trainingSession.getAttendees().add(user);
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Act
        trainingSessionService.unregisterUserFromTrainingSession(sessionId, userId);

        // Assert
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Should throw exception when training session not found during unregistration")
    void shouldThrowException_WhenTrainingSessionNotFoundDuringUnregistration() {
        // Arrange
        Long nonExistentSessionId = 999L;
        when(trainingSessionRepository.findById(nonExistentSessionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.unregisterUserFromTrainingSession(nonExistentSessionId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("Training session not found with id: " + nonExistentSessionId);
                });
        
        verify(trainingSessionRepository, times(1)).findById(nonExistentSessionId);
        verify(userProfileRepository, never()).findById(any());
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found during unregistration")
    void shouldThrowException_WhenUserNotFoundDuringUnregistration() {
        // Arrange
        Long nonExistentUserId = 999L;
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.unregisterUserFromTrainingSession(sessionId, nonExistentUserId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentUserId);
                });
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(nonExistentUserId);
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not registered for training session")
    void shouldThrowException_WhenUserNotRegisteredForTrainingSession() {
        // Arrange
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.of(trainingSession));
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> trainingSessionService.unregisterUserFromTrainingSession(sessionId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User is not registered for this training session");
                });
        
        verify(trainingSessionRepository, times(1)).findById(sessionId);
        verify(userProfileRepository, times(1)).findById(userId);
        verify(trainingSessionRepository, never()).save(any());
    }
}



