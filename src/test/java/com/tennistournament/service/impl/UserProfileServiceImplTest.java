package com.tennistournament.service.impl;

import com.tennistournament.dto.UserProfileRequest;
import com.tennistournament.dto.UserProfileResponse;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.UserProfileRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService Unit Tests")
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private Long userId;
    private UserProfileRequest validRequest;
    private UserProfile savedUser;

    @BeforeEach
    void setUp() {
        userId = 1L;
        validRequest = new UserProfileRequest(
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "INTERMEDIATE"
        );

        savedUser = new UserProfile();
        savedUser.setId(userId);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setSkillLevel("INTERMEDIATE");
        savedUser.setRegisteredTournaments(new java.util.HashSet<>());
        savedUser.setRegisteredTrainingSessions(new java.util.HashSet<>());
    }

    @Test
    @DisplayName("Should create user when valid data provided")
    void shouldCreateUser_WhenValidData() {
        // Arrange
        when(userProfileRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedUser);

        // Act
        UserProfileResponse result = userProfileService.createUser(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getSkillLevel()).isEqualTo("INTERMEDIATE");
        assertThat(result.getRegisteredTournamentIds()).isEmpty();
        assertThat(result.getRegisteredTrainingSessionIds()).isEmpty();
        
        verify(userProfileRepository, times(1)).findByUsername("testuser");
        verify(userProfileRepository, times(1)).findByEmail("test@example.com");
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        UserProfile existingUser = new UserProfile();
        existingUser.setUsername("testuser");
        when(userProfileRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.createUser(validRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Username already exists: testuser");
                });
        
        verify(userProfileRepository, times(1)).findByUsername("testuser");
        verify(userProfileRepository, never()).findByEmail(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userProfileRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        UserProfile existingUser = new UserProfile();
        existingUser.setEmail("test@example.com");
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.createUser(validRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Email already exists: test@example.com");
                });
        
        verify(userProfileRepository, times(1)).findByUsername("testuser");
        verify(userProfileRepository, times(1)).findByEmail("test@example.com");
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create user with null optional fields")
    void shouldCreateUser_WhenOptionalFieldsAreNull() {
        // Arrange
        UserProfileRequest requestWithNulls = new UserProfileRequest(
                "testuser",
                "test@example.com",
                null,
                null,
                null
        );
        savedUser.setFirstName(null);
        savedUser.setLastName(null);
        savedUser.setSkillLevel(null);
        
        when(userProfileRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedUser);

        // Act
        UserProfileResponse result = userProfileService.createUser(requestWithNulls);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getSkillLevel()).isNull();
        
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Should return all users when users exist")
    void shouldGetAllUsers_WhenUsersExist() {
        // Arrange
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRegisteredTournaments(new java.util.HashSet<>());
        user1.setRegisteredTrainingSessions(new java.util.HashSet<>());

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setRegisteredTournaments(new java.util.HashSet<>());
        user2.setRegisteredTrainingSessions(new java.util.HashSet<>());

        List<UserProfile> users = Arrays.asList(user1, user2);
        when(userProfileRepository.findAll()).thenReturn(users);

        // Act
        List<UserProfileResponse> result = userProfileService.getAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
        
        verify(userProfileRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldGetAllUsers_WhenNoUsersExist() {
        // Arrange
        when(userProfileRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<UserProfileResponse> result = userProfileService.getAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(userProfileRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user when valid id provided")
    void shouldGetUserById_WhenValidId() {
        // Arrange
        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        // Act
        UserProfileResponse result = userProfileService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by id")
    void shouldThrowException_WhenUserNotFoundById() {
        // Arrange
        Long nonExistentId = 999L;
        when(userProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getUserById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentId);
                });
        
        verify(userProfileRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should update user when valid data and id provided")
    void shouldUpdateUser_WhenValidDataAndId() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "updateduser",
                "updated@example.com",
                "Jane",
                "Smith",
                "ADVANCED"
        );
        
        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(userId);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setSkillLevel("ADVANCED");
        updatedUser.setRegisteredTournaments(new java.util.HashSet<>());
        updatedUser.setRegisteredTrainingSessions(new java.util.HashSet<>());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userProfileRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedUser);

        // Act
        UserProfileResponse result = userProfileService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getSkillLevel()).isEqualTo("ADVANCED");
        
        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, times(1)).findByUsername("updateduser");
        verify(userProfileRepository, times(1)).findByEmail("updated@example.com");
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Should update user when username and email unchanged")
    void shouldUpdateUser_WhenUsernameAndEmailUnchanged() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "testuser", // Same username
                "test@example.com", // Same email
                "Jane",
                "Smith",
                "ADVANCED"
        );
        
        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(userId);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setSkillLevel("ADVANCED");
        updatedUser.setRegisteredTournaments(new java.util.HashSet<>());
        updatedUser.setRegisteredTrainingSessions(new java.util.HashSet<>());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedUser);

        // Act
        UserProfileResponse result = userProfileService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("Jane");
        
        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).findByUsername(any());
        verify(userProfileRepository, never()).findByEmail(any());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowException_WhenUpdatingNonExistentUser() {
        // Arrange
        Long nonExistentId = 999L;
        UserProfileRequest updateRequest = new UserProfileRequest(
                "updateduser",
                "updated@example.com",
                "Jane",
                "Smith",
                "ADVANCED"
        );
        when(userProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUser(nonExistentId, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).contains("User not found with id: " + nonExistentId);
                });
        
        verify(userProfileRepository, times(1)).findById(nonExistentId);
        verify(userProfileRepository, never()).findByUsername(any());
        verify(userProfileRepository, never()).findByEmail(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new username already exists")
    void shouldThrowException_WhenNewUsernameAlreadyExists() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "existinguser", // Different username that exists
                "test@example.com",
                "Jane",
                "Smith",
                "ADVANCED"
        );
        
        UserProfile existingUser = new UserProfile();
        existingUser.setId(999L);
        existingUser.setUsername("existinguser");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUser(userId, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Username already exists: existinguser");
                });
        
        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, times(1)).findByUsername("existinguser");
        verify(userProfileRepository, never()).findByEmail(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new email already exists")
    void shouldThrowException_WhenNewEmailAlreadyExists() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "testuser",
                "existing@example.com", // Different email that exists
                "Jane",
                "Smith",
                "ADVANCED"
        );
        
        UserProfile existingUser = new UserProfile();
        existingUser.setId(999L);
        existingUser.setEmail("existing@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.updateUser(userId, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex = (ResponseStatusException) exception;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).contains("Email already exists: existing@example.com");
                });
        
        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).findByUsername(any());
        verify(userProfileRepository, times(1)).findByEmail("existing@example.com");
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user when only username changed")
    void shouldUpdateUser_WhenOnlyUsernameChanged() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "newusername",
                "test@example.com", // Same email
                "John",
                "Doe",
                "INTERMEDIATE"
        );
        
        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(userId);
        updatedUser.setUsername("newusername");
        updatedUser.setEmail("test@example.com");
        updatedUser.setRegisteredTournaments(new java.util.HashSet<>());
        updatedUser.setRegisteredTrainingSessions(new java.util.HashSet<>());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.findByUsername("newusername")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedUser);

        // Act
        UserProfileResponse result = userProfileService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newusername");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userProfileRepository, times(1)).findByUsername("newusername");
        verify(userProfileRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should update user when only email changed")
    void shouldUpdateUser_WhenOnlyEmailChanged() {
        // Arrange
        UserProfileRequest updateRequest = new UserProfileRequest(
                "testuser", // Same username
                "newemail@example.com",
                "John",
                "Doe",
                "INTERMEDIATE"
        );
        
        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(userId);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setRegisteredTournaments(new java.util.HashSet<>());
        updatedUser.setRegisteredTrainingSessions(new java.util.HashSet<>());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userProfileRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedUser);

        // Act
        UserProfileResponse result = userProfileService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("newemail@example.com");
        
        verify(userProfileRepository, never()).findByUsername(any());
        verify(userProfileRepository, times(1)).findByEmail("newemail@example.com");
    }
}



