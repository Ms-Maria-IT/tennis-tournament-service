# Feign Error Handling Fix

## Problem
When the club microservice returned a 404 (club not found), the Feign client was throwing a `FeignException`, which was being caught by the `GlobalExceptionHandler` and converted to a 500 Internal Server Error instead of preserving the 404 status code.

## Root Cause
- Feign's default error decoder throws `FeignException` for all non-2xx responses
- `GlobalExceptionHandler` was catching `FeignException` in the generic `Exception` handler
- Generic exception handler always returns 500, losing the original status code

## Solution

### 1. Custom Feign Error Decoder (`FeignErrorDecoder`)
Created a custom error decoder that:
- Intercepts Feign responses with error status codes
- Converts them to `ResponseStatusException` with the appropriate HTTP status
- Preserves 404, 4xx, and 5xx status codes from the microservice

**Key Features:**
- 404 errors → `ResponseStatusException(HttpStatus.NOT_FOUND)`
- 4xx client errors → `ResponseStatusException` with preserved status
- 5xx server errors → `ResponseStatusException` with preserved status
- Other errors → Uses default Feign decoder

### 2. Feign Configuration Update
Added the custom error decoder to `FeignConfig`:
```java
@Bean
public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
}
```

### 3. Service Implementation Updates
Updated `TournamentServiceImpl` and `TrainingSessionServiceImpl` to:
- Catch `ResponseStatusException` and re-throw it (preserves status code)
- Let the exception propagate to `GlobalExceptionHandler`
- `GlobalExceptionHandler` already handles `ResponseStatusException` correctly

### 4. GlobalExceptionHandler Enhancement
Added explicit `FeignException` handler as a safety net:
- If any `FeignException` slips through, it extracts the status code
- Returns the appropriate HTTP status instead of always returning 500

## Flow Diagram

### Before Fix:
```
Club Service (404) 
  → Feign Client (FeignException) 
  → GlobalExceptionHandler (Exception handler) 
  → Client receives 500 ❌
```

### After Fix:
```
Club Service (404) 
  → Feign Client (FeignException) 
  → FeignErrorDecoder (ResponseStatusException with 404) 
  → GlobalExceptionHandler (ResponseStatusException handler) 
  → Client receives 404 ✅
```

## Testing

### Test Case 1: Club Not Found (404)
1. Call `POST /api/tournaments` with non-existent `clubId`
2. **Expected**: 404 Not Found
3. **Before**: 500 Internal Server Error ❌
4. **After**: 404 Not Found ✅

### Test Case 2: Club Service Unavailable (503)
1. Stop club service
2. Call `POST /api/tournaments` with valid `clubId`
3. **Expected**: 503 Service Unavailable (from fallback)
4. **After**: 503 Service Unavailable ✅

### Test Case 3: Valid Club (200)
1. Call `POST /api/tournaments` with valid `clubId`
2. **Expected**: 201 Created
3. **After**: 201 Created ✅

## Files Changed

1. **New**: `src/main/java/com/tennistournament/config/FeignErrorDecoder.java`
   - Custom error decoder implementation

2. **Updated**: `src/main/java/com/tennistournament/config/FeignConfig.java`
   - Added `errorDecoder()` bean

3. **Updated**: `src/main/java/com/tennistournament/service/impl/TournamentServiceImpl.java`
   - Updated to handle `ResponseStatusException` from Feign

4. **Updated**: `src/main/java/com/tennistournament/service/impl/TrainingSessionServiceImpl.java`
   - Updated to handle `ResponseStatusException` from Feign

5. **Updated**: `src/main/java/com/tennistournament/exception/GlobalExceptionHandler.java`
   - Added explicit `FeignException` handler

## Benefits

✅ **Status Code Preservation**: 404 from microservice becomes 404 to client  
✅ **Proper Error Messages**: Error messages are preserved and meaningful  
✅ **Consistent API**: Monolith API behaves consistently with microservice  
✅ **Better Debugging**: Correct status codes make debugging easier  
✅ **Safety Net**: Multiple layers of error handling ensure robustness  

## Notes

- The `mapToResponse()` methods in services still use try-catch for graceful degradation when fetching club names
- This is intentional - if club service is unavailable during response mapping, we set club name to null rather than failing the entire request
- The fallback (`ClubServiceClientFallback`) is only used for circuit breaker scenarios, not for 404 responses
