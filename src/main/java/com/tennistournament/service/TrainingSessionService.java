package com.tennistournament.service;

import com.tennistournament.dto.TrainingSessionRequest;
import com.tennistournament.dto.TrainingSessionResponse;

import java.util.List;

public interface TrainingSessionService {
    TrainingSessionResponse createTrainingSession(Long clubId, TrainingSessionRequest request);
    List<TrainingSessionResponse> getAllTrainingSessions(Long clubId);
    TrainingSessionResponse getTrainingSessionById(Long id);
    void registerUserForTrainingSession(Long sessionId, Long userId);
    void unregisterUserFromTrainingSession(Long sessionId, Long userId);
}

