package com.tennistournament.service.impl;

import com.tennistournament.dto.TrainingSessionRequest;
import com.tennistournament.dto.TrainingSessionResponse;
import com.tennistournament.model.TennisClub;
import com.tennistournament.model.TrainingSession;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.TennisClubRepository;
import com.tennistournament.repository.TrainingSessionRepository;
import com.tennistournament.repository.UserProfileRepository;
import com.tennistournament.service.TrainingSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TennisClubRepository tennisClubRepository;
    private final UserProfileRepository userProfileRepository;

    public TrainingSessionServiceImpl(TrainingSessionRepository trainingSessionRepository,
                                     TennisClubRepository tennisClubRepository,
                                     UserProfileRepository userProfileRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.tennisClubRepository = tennisClubRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public TrainingSessionResponse createTrainingSession(Long clubId, TrainingSessionRequest request) {
        TennisClub club = tennisClubRepository.findById(clubId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tennis club not found with id: " + clubId));
        
        // Validate date range
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "End date and time must be after start date and time");
        }
        
        TrainingSession session = new TrainingSession();
        session.setName(request.getName());
        session.setDescription(request.getDescription());
        session.setStartDateTime(request.getStartDateTime());
        session.setEndDateTime(request.getEndDateTime());
        session.setMaxAttendees(request.getMaxAttendees());
        session.setCoachName(request.getCoachName());
        session.setTennisClub(club);
        
        TrainingSession savedSession = trainingSessionRepository.save(session);
        return mapToResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> getAllTrainingSessions(Long clubId) {
        List<TrainingSession> sessions;
        if (clubId != null) {
            sessions = trainingSessionRepository.findByTennisClubId(clubId);
        } else {
            sessions = trainingSessionRepository.findAll();
        }
        return sessions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingSessionResponse getTrainingSessionById(Long id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Training session not found with id: " + id));
        return mapToResponse(session);
    }

    @Override
    public void registerUserForTrainingSession(Long sessionId, Long userId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Training session not found with id: " + sessionId));
        
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + userId));
        
        // Check if user is already registered
        if (session.getAttendees().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "User is already registered for this training session");
        }
        
        // Check if max attendees limit has been reached
        if (session.getMaxAttendees() != null && 
            session.getAttendees().size() >= session.getMaxAttendees()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Training session has reached maximum number of attendees");
        }
        
        // TODO: Check for scheduling conflicts (user cannot register for two training sessions at the same time)
        
        session.getAttendees().add(user);
        trainingSessionRepository.save(session);
    }

    @Override
    public void unregisterUserFromTrainingSession(Long sessionId, Long userId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Training session not found with id: " + sessionId));
        
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + userId));
        
        if (!session.getAttendees().contains(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "User is not registered for this training session");
        }
        
        session.getAttendees().remove(user);
        trainingSessionRepository.save(session);
    }

    private TrainingSessionResponse mapToResponse(TrainingSession session) {
        TrainingSessionResponse response = new TrainingSessionResponse();
        response.setId(session.getId());
        response.setName(session.getName());
        response.setDescription(session.getDescription());
        response.setStartDateTime(session.getStartDateTime());
        response.setEndDateTime(session.getEndDateTime());
        response.setMaxAttendees(session.getMaxAttendees());
        response.setCoachName(session.getCoachName());
        
        if (session.getTennisClub() != null) {
            response.setTennisClubId(session.getTennisClub().getId());
            response.setTennisClubName(session.getTennisClub().getName());
        }
        
        if (session.getAttendees() != null) {
            response.setAttendeeIds(session.getAttendees().stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList()));
            response.setCurrentAttendeeCount(session.getAttendees().size());
        }
        
        return response;
    }
}

