package com.tennistournament.repository;

import com.tennistournament.model.TennisClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TODO: TEMPORARY - TennisClub functionality has been extracted to tennis-club-service
 * TODO: Remove this repository once REST client integration is implemented
 * TODO: Tournament and TrainingSession services should call tennis-club-service REST API instead
 */
@Repository
@Deprecated
public interface TennisClubRepository extends JpaRepository<TennisClub, Long> {
}
