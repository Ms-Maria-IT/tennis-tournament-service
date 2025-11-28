package com.tennistournament.service;

import com.tennistournament.dto.TennisClubRequest;
import com.tennistournament.dto.TennisClubResponse;

import java.util.List;

public interface TennisClubService {
    TennisClubResponse createClub(TennisClubRequest request);
    List<TennisClubResponse> getAllClubs();
    TennisClubResponse getClubById(Long id);
    TennisClubResponse updateClub(Long id, TennisClubRequest request);
    void deleteClub(Long id);
}

