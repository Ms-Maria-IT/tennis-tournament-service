package com.tennistournament.service;

import com.tennistournament.dto.TournamentRequest;
import com.tennistournament.dto.TournamentResponse;

import java.util.List;

public interface TournamentService {
    TournamentResponse createTournament(Long clubId, TournamentRequest request);
    List<TournamentResponse> getAllTournaments(Long clubId);
    TournamentResponse getTournamentById(Long id);
    void registerUserForTournament(Long tournamentId, Long userId);
    void unregisterUserFromTournament(Long tournamentId, Long userId);
}

