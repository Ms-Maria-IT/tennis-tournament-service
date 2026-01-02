package com.tennistournament.service.impl;

import com.tennistournament.client.ClubServiceClient;
import com.tennistournament.client.dto.ClubResponse;
import com.tennistournament.dto.TournamentRequest;
import com.tennistournament.dto.TournamentResponse;
import com.tennistournament.model.Tournament;
import com.tennistournament.model.UserProfile;
import com.tennistournament.repository.TournamentRepository;
import com.tennistournament.repository.UserProfileRepository;
import com.tennistournament.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserProfileRepository userProfileRepository;
    private final ClubServiceClient clubServiceClient;

    public TournamentServiceImpl(TournamentRepository tournamentRepository,
                                 UserProfileRepository userProfileRepository,
                                 ClubServiceClient clubServiceClient) {
        this.tournamentRepository = tournamentRepository;
        this.userProfileRepository = userProfileRepository;
        this.clubServiceClient = clubServiceClient;
    }

    @Override
    public TournamentResponse createTournament(Long clubId, TournamentRequest request) {
        // Validate club exists via club service
        // FeignErrorDecoder will throw ResponseStatusException for 4xx/5xx responses
        // which will be handled by GlobalExceptionHandler and preserve the status code
        try {
            ResponseEntity<ClubResponse> clubResponse = clubServiceClient.getClubById(clubId);
            
            // Check if response body is null (shouldn't happen for 200, but safety check)
            if (clubResponse.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tennis club not found with id: " + clubId);
            }
        } catch (ResponseStatusException e) {
            // Re-throw to preserve status code (404, 503, etc.)
            throw e;
        }
        
        // Validate date range
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "End date and time must be after start date and time");
        }
        
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setStartDateTime(request.getStartDateTime());
        tournament.setEndDateTime(request.getEndDateTime());
        tournament.setMaxParticipants(request.getMaxParticipants());
        tournament.setTennisClubId(clubId);
        
        Tournament savedTournament = tournamentRepository.save(tournament);
        return mapToResponse(savedTournament);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments(Long clubId) {
        List<Tournament> tournaments;
        if (clubId != null) {
            tournaments = tournamentRepository.findByTennisClubId(clubId);
        } else {
            tournaments = tournamentRepository.findAll();
        }
        return tournaments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tournament not found with id: " + id));
        return mapToResponse(tournament);
    }

    @Override
    public void registerUserForTournament(Long tournamentId, Long userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tournament not found with id: " + tournamentId));
        
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + userId));
        
        // Check if user is already registered
        if (tournament.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "User is already registered for this tournament");
        }
        
        // Check if max participants limit has been reached
        if (tournament.getMaxParticipants() != null && 
            tournament.getParticipants().size() >= tournament.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Tournament has reached maximum number of participants");
        }
        
        // TODO: Check for scheduling conflicts (user cannot register for two tournaments at the same time)
        
        tournament.getParticipants().add(user);
        tournamentRepository.save(tournament);
    }

    @Override
    public void unregisterUserFromTournament(Long tournamentId, Long userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tournament not found with id: " + tournamentId));
        
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + userId));
        
        if (!tournament.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "User is not registered for this tournament");
        }
        
        tournament.getParticipants().remove(user);
        tournamentRepository.save(tournament);
    }

    private TournamentResponse mapToResponse(Tournament tournament) {
        TournamentResponse response = new TournamentResponse();
        response.setId(tournament.getId());
        response.setName(tournament.getName());
        response.setStartDateTime(tournament.getStartDateTime());
        response.setEndDateTime(tournament.getEndDateTime());
        response.setMaxParticipants(tournament.getMaxParticipants());
        
        // Fetch club information from club service
        if (tournament.getTennisClubId() != null) {
            response.setTennisClubId(tournament.getTennisClubId());
            try {
                ResponseEntity<ClubResponse> clubResponse = clubServiceClient.getClubById(tournament.getTennisClubId());
                if (clubResponse.getStatusCode() == HttpStatus.OK && clubResponse.getBody() != null) {
                    response.setTennisClubName(clubResponse.getBody().getName());
                }
            } catch (Exception e) {
                // If club service is unavailable, set name to null
                // This allows the response to still be returned
                response.setTennisClubName(null);
            }
        }
        
        if (tournament.getParticipants() != null) {
            response.setParticipantIds(tournament.getParticipants().stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList()));
            response.setCurrentParticipantCount(tournament.getParticipants().size());
        }
        
        return response;
    }
}

