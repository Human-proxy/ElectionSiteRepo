# Author
This document was created by Dominik Krystul.

# Election Data Caching Implementation

## Problem
The XML parser was re-running on every homepage refresh, parsing all election data (parties, candidates, results) from scratch. This caused:
- Slow response times
- Excessive logging output
- Unnecessary CPU/memory usage
- Poor user experience

## Solution
Implemented in-memory caching in `DutchElectionService`:

### Changes Made

#### 1. `DutchElectionService.java`
- Added `ConcurrentHashMap<String, Election>` cache to store parsed elections
- Cache key format: `"electionId:folderName"` (e.g., `"TK2023:TK2023_HvA_UvA"`)
- Modified `readResults()` to:
  1. Check cache first
  2. Return cached data if available
  3. Parse and cache on first request only
- Added cache management methods:
  - `clearCache()` - clears all cached elections
  - `clearCacheFor(electionId, folderName)` - clears specific election

#### 2. `WebsiteController.java`
- Added `POST /api/elections/cache/clear` endpoint for development
- Allows forcing a re-parse during testing

## How It Works

### First Request to `/api/elections/results`:
```
1. Controller calls electionService.readResults("TK2023", "TK2023_HvA_UvA")
2. Service checks cache for key "TK2023:TK2023_HvA_UvA" → NOT FOUND
3. Service parses all XML files (candidates, parties, results)
4. Service calculates seats per party
5. Service stores Election object in cache
6. Service returns Election object to controller
```

### Subsequent Requests:
```
1. Controller calls electionService.readResults("TK2023", "TK2023_HvA_UvA")
2. Service checks cache for key "TK2023:TK2023_HvA_UvA" → FOUND!
3. Service returns cached Election object immediately
```

## Benefits
- ✅ **Instant response** after first parse (no re-parsing)
- ✅ **Clean logs** - only parse logs on first request
- ✅ **Lower resource usage** - no redundant XML parsing
- ✅ **Thread-safe** - uses `ConcurrentHashMap`
- ✅ **Development-friendly** - cache can be cleared via API

## Testing

### Verify Caching Works:
1. Start the backend
2. Open browser to `http://localhost:8080/api/elections/results`
3. Check logs - you should see full parsing output
4. Refresh the page
5. Check logs - should only see "Returning cached election data"

### Clear Cache (Force Re-parse):
```bash
curl -X POST http://localhost:8080/api/elections/cache/clear
```

### View Homepage Chart:
1. Frontend: `http://localhost:5173`
2. First load: parses data (check backend logs)
3. Refresh: uses cache (no parse logs)

## Future Improvements
- Integrate with a database: Store parsed election data in a database for persistence and scalability.
- Implement database-level caching: Ensure that repeated requests do not always trigger database queries, but use an in-memory cache for frequently accessed data.
- Add cache metrics/monitoring.
- Secure the cache-clear endpoint in production.

## Code Locations
- Service: `election-backend/src/main/java/nl/hva/dederdekamer/election_backend/XMLParser/service/DutchElectionService.java`
- Controller: `election-backend/src/main/java/nl/hva/dederdekamer/election_backend/api/WebsiteController.java`
