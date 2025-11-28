package com.tennistournament.repository;

import com.tennistournament.model.TennisClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TennisClubRepository extends JpaRepository<TennisClub, Long> {
}

