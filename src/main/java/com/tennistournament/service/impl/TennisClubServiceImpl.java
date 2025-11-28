package com.tennistournament.service.impl;

import com.tennistournament.dto.TennisClubRequest;
import com.tennistournament.dto.TennisClubResponse;
import com.tennistournament.model.TennisClub;
import com.tennistournament.repository.TennisClubRepository;
import com.tennistournament.service.TennisClubService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TennisClubServiceImpl implements TennisClubService {

    private final TennisClubRepository tennisClubRepository;

    public TennisClubServiceImpl(TennisClubRepository tennisClubRepository) {
        this.tennisClubRepository = tennisClubRepository;
    }

    @Override
    public TennisClubResponse createClub(TennisClubRequest request) {
        TennisClub club = new TennisClub();
        club.setName(request.getName());
        club.setAddress(request.getAddress());
        TennisClub savedClub = tennisClubRepository.save(club);
        return mapToResponse(savedClub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TennisClubResponse> getAllClubs() {
        return tennisClubRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TennisClubResponse getClubById(Long id) {
        TennisClub club = tennisClubRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tennis club not found with id: " + id));
        return mapToResponse(club);
    }

    @Override
    public TennisClubResponse updateClub(Long id, TennisClubRequest request) {
        TennisClub club = tennisClubRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Tennis club not found with id: " + id));
        club.setName(request.getName());
        club.setAddress(request.getAddress());
        TennisClub updatedClub = tennisClubRepository.save(club);
        return mapToResponse(updatedClub);
    }

    @Override
    public void deleteClub(Long id) {
        if (!tennisClubRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Tennis club not found with id: " + id);
        }
        tennisClubRepository.deleteById(id);
    }

    private TennisClubResponse mapToResponse(TennisClub club) {
        TennisClubResponse response = new TennisClubResponse();
        response.setId(club.getId());
        response.setName(club.getName());
        response.setAddress(club.getAddress());
        
        if (club.getCourts() != null) {
            response.setCourtIds(club.getCourts().stream()
                    .map(court -> court.getId())
                    .collect(Collectors.toList()));
        }
        
        if (club.getTournaments() != null) {
            response.setTournamentIds(club.getTournaments().stream()
                    .map(tournament -> tournament.getId())
                    .collect(Collectors.toList()));
        }
        
        if (club.getTrainingSessions() != null) {
            response.setTrainingSessionIds(club.getTrainingSessions().stream()
                    .map(session -> session.getId())
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
}

