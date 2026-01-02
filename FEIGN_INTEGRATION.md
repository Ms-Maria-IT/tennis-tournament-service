# Feign Client Integration - Tennis Club Service

## Overview
The monolith has been integrated with the `tennis-club-service` microservice using Spring Cloud OpenFeign. All club-related code has been removed from the monolith, and services now communicate with the club service via REST calls.

## Changes Made

### 1. Dependencies Added (`build.gradle`)
- `spring-cloud-starter-openfeign` - Feign client support
- `resilience4j-spring-boot3` - Circuit breaker and retry patterns
- `resilience4j-feign` - Feign integration with Resilience4j
- Spring Cloud BOM for dependency management

### 2. Removed from Monolith
- ✅ `TennisClubController` - Deleted
- ✅ `TennisClubService` and `TennisClubServiceImpl` - Deleted
- ✅ `TennisClubRepository` - Deleted
- ✅ `TennisClub` entity - Deleted
- ✅ `Court` entity - Deleted (moved to club service)

### 3. Entity Updates
- **Tournament**: Changed from `@ManyToOne TennisClub` to `Long tennisClubId`
- **TrainingSession**: Changed from `@ManyToOne TennisClub` to `Long tennisClubId`
- Repositories already use `findByTennisClubId()` which works with the new structure

### 4. Feign Client Implementation

#### `ClubServiceClient` Interface
- Feign client interface for calling club service
- Base URL: `http://localhost:8081` (configurable via `club.service.url`)
- Methods:
  - `getAllClubs()` - Get all clubs
  - `getClubById(Long id)` - Get club by ID

#### `ClubServiceClientFallback`
- Fallback implementation for circuit breaker pattern
- Returns empty list or SERVICE_UNAVAILABLE when club service is down
- Prevents cascading failures

#### `FeignConfig`
- Configures Feign logging, retry, and timeout settings
- Retry: 3 attempts with 1 second intervals
- Timeout: 5s connect, 10s read

### 5. Service Updates

#### `TournamentServiceImpl`
- Removed `TennisClubRepository` dependency
- Added `ClubServiceClient` dependency
- Validates club existence via Feign client before creating tournaments
- Fetches club name from service when mapping responses
- Handles service unavailability gracefully

#### `TrainingSessionServiceImpl`
- Removed `TennisClubRepository` dependency
- Added `ClubServiceClient` dependency
- Validates club existence via Feign client before creating sessions
- Fetches club name from service when mapping responses
- Handles service unavailability gracefully

### 6. Configuration (`application.properties`)

```properties
# Feign Client Configuration
club.service.url=http://localhost:8081
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000
feign.client.config.default.loggerLevel=basic

# Resilience4j Configuration
resilience4j.circuitbreaker.instances.clubServiceClient.failureRateThreshold=50
resilience4j.circuitbreaker.instances.clubServiceClient.waitDurationInOpenState=10000
resilience4j.circuitbreaker.instances.clubServiceClient.slidingWindowSize=10
resilience4j.retry.instances.clubServiceClient.maxAttempts=3
resilience4j.retry.instances.clubServiceClient.waitDuration=1000
```

### 7. Application Class
- Added `@EnableFeignClients` annotation to enable Feign client scanning

## How It Works

1. **Creating Tournament/Training Session**:
   - Service calls `clubServiceClient.getClubById(clubId)`
   - If club exists (200 OK), proceeds with creation
   - If club not found (404), throws NOT_FOUND exception
   - If service unavailable (503), throws SERVICE_UNAVAILABLE exception

2. **Fetching Tournament/Training Session**:
   - Service loads entity from database (with `clubId`)
   - Calls `clubServiceClient.getClubById(clubId)` to get club name
   - If service unavailable, club name is set to null (graceful degradation)

3. **Circuit Breaker**:
   - After 50% failure rate in 10 requests, circuit opens
   - Fallback is called automatically
   - Circuit closes after 10 seconds

## Testing

### Prerequisites
1. Start `tennis-club-service` on port 8081
2. Start monolith on port 8080

### Test Scenarios
1. **Normal Operation**: Club service available - all operations work
2. **Club Not Found**: Returns 404 with appropriate message
3. **Service Unavailable**: Returns 503 with fallback message
4. **Circuit Breaker**: After failures, fallback is used automatically

## Future Enhancements

1. **Service Discovery**: Replace hardcoded URL with Eureka/Consul
2. **Caching**: Cache club information to reduce service calls
3. **Bulk Operations**: Add methods to fetch multiple clubs at once
4. **Metrics**: Add custom metrics for Feign calls
5. **Distributed Tracing**: Add Sleuth/Zipkin for request tracing

## Notes

- Club service must be running for tournament/training session creation
- Club name in responses may be null if service is unavailable (graceful degradation)
- All club CRUD operations must be done via club service API
- Database no longer contains `tennis_clubs` or `courts` tables
