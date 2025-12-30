# Migration Notes: Tennis Club Service Extraction

## Overview
The TennisClub functionality has been extracted from the monolithic application into a separate microservice: `tennis-club-service`.

## What Was Extracted

### New Microservice: `tennis-club-service/`
- **Port**: 8081
- **Database**: H2 in-memory (`club_service_db`)
- **Package**: `com.tennistournament.clubservice`

### Extracted Components:
- `TennisClubController` → `/api/clubs` endpoints
- `TennisClubService` and `TennisClubServiceImpl`
- `TennisClubRepository`
- `TennisClub` entity
- `Court` entity and `CourtRepository`
- `CourtController` → `/api/clubs/{clubId}/courts` endpoints
- `TennisClubRequest` and `TennisClubResponse` DTOs
- `CourtRequest` and `CourtResponse` DTOs
- All related unit tests

## Remaining Work in Monolith

### TournamentService & TrainingSessionService
These services still have dependencies on `TennisClubRepository` and `TennisClub` entity. 

**TODO Items:**
1. Remove `TennisClubRepository` dependency
2. Replace club validation with REST calls to `tennis-club-service`
3. Update `Tournament` and `TrainingSession` entities to store `clubId` instead of `TennisClub` reference
4. Implement REST client (Feign or WebClient) to call club service:
   - Endpoint: `http://localhost:8081/api/clubs/{clubId}`
   - Validate club exists before creating tournaments/training sessions

### Entity Updates Needed:
- `Tournament` entity: Replace `@ManyToOne TennisClub` with `Long clubId`
- `TrainingSession` entity: Replace `@ManyToOne TennisClub` with `Long clubId`
- Update repositories to use `clubId` instead of `tennisClubId`

## Running the Services

### Tennis Club Service (New Microservice)
```bash
cd tennis-club-service
./gradlew bootRun
```
- Runs on: http://localhost:8081
- API Docs: http://localhost:8081/swagger-ui.html

### Original Monolith
```bash
./gradlew bootRun
```
- Runs on: http://localhost:8080
- **Note**: Club endpoints removed - will need REST client integration

## Next Steps

1. **Implement REST Client** in monolith to call club service
2. **Update Tournament/TrainingSession entities** to use `clubId` instead of entity reference
3. **Remove TennisClub entity** from monolith completely
4. **Add service discovery** (Eureka/Consul) for production
5. **Add circuit breakers** (Resilience4j) for resilience
6. **Update integration tests** to mock club service calls

## API Contract

The club service maintains the same API contract as before:
- `POST /api/clubs` - Create club
- `GET /api/clubs` - Get all clubs
- `GET /api/clubs/{id}` - Get club by ID
- `PUT /api/clubs/{id}` - Update club
- `DELETE /api/clubs/{id}` - Delete club
- `POST /api/clubs/{clubId}/courts` - Add court
- `GET /api/clubs/{clubId}/courts` - Get club's courts

This ensures backward compatibility with existing clients.
