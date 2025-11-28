package com.tennistournament.service.impl;

import com.tennistournament.dto.UserProfileRequest;
import com.tennistournament.dto.UserProfileResponse;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.UserProfileRepository;
import com.tennistournament.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileResponse createUser(UserProfileRequest request) {
        // Check if username already exists
        if (userProfileRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userProfileRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Email already exists: " + request.getEmail());
        }
        
        UserProfile user = new UserProfile();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setSkillLevel(request.getSkillLevel());
        
        UserProfile savedUser = userProfileRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateUser(Long id, UserProfileRequest request) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + id));
        
        // Check if username is being changed and if new username already exists
        if (!user.getUsername().equals(request.getUsername())) {
            if (userProfileRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                        "Username already exists: " + request.getUsername());
            }
        }
        
        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(request.getEmail())) {
            if (userProfileRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                        "Email already exists: " + request.getEmail());
            }
        }
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setSkillLevel(request.getSkillLevel());
        
        UserProfile updatedUser = userProfileRepository.save(user);
        return mapToResponse(updatedUser);
    }

    private UserProfileResponse mapToResponse(UserProfile user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setSkillLevel(user.getSkillLevel());
        
        if (user.getRegisteredTournaments() != null) {
            response.setRegisteredTournamentIds(user.getRegisteredTournaments().stream()
                    .map(tournament -> tournament.getId())
                    .collect(Collectors.toList()));
        }
        
        if (user.getRegisteredTrainingSessions() != null) {
            response.setRegisteredTrainingSessionIds(user.getRegisteredTrainingSessions().stream()
                    .map(session -> session.getId())
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
}

