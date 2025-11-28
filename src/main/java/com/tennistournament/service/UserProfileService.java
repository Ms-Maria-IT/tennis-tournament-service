package com.tennistournament.service;

import com.tennistournament.dto.UserProfileRequest;
import com.tennistournament.dto.UserProfileResponse;

import java.util.List;

public interface UserProfileService {
    UserProfileResponse createUser(UserProfileRequest request);
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse getUserById(Long id);
    UserProfileResponse updateUser(Long id, UserProfileRequest request);
}

