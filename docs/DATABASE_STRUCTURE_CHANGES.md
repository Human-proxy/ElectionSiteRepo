# Database Structure Changes - Election Result Map Branch

## Overview
This branch (`feature/election-result-map`) introduces a **major refactoring** of the database structure to support detailed election result tracking at multiple geographic levels: **national**, **constituency**, **municipality**, and **polling bureau**.

The new structure separates **geographic entities** (static locations) from **result entities** (vote data per election), enabling efficient querying and aggregation of election results.

---

## Key Changes

### 1. **Geographic Hierarchy (Static Entities)**
These entities represent the physical/administrative structure and are **independent of elections**:

```
Election
  └─ Constituency (e.g., "HSB9" - Kieskring Amsterdam)
       └─ Municipality (e.g., "0363" - Amsterdam)
            └─ Polling Bureau (e.g., "0363::SB1" - Stembureau 1)
```

**Key Points:**
- **Constituency** entities are now persisted in the database (previously transient)
- **Municipality** is a standalone entity with a 4-digit code (e.g., `0363` for Amsterdam)
- **PollingBureau** represents individual voting stations within a municipality
- Each level has a **one-to-many relationship** with the level below

### 2. **Result Entities (Vote Data)**
New entities track **votes per party at each geographic level**:

- **PartyResult** - National-level results (total votes, seats, elected status)
- **MunicipalityResult** - Municipality-level results (party votes in a specific municipality)
- **PollingBureauResult** - Polling bureau-level results (most granular vote data)

Each result entity has a **composite unique constraint** to prevent duplicate data:
```sql
UNIQUE (election_id, municipality_id, party_id)  -- MunicipalityResult
UNIQUE (election_id, polling_bureau_id, party_id)  -- PollingBureauResult
```

### 3. **Party Model Changes**
- **Party ID changed from String to Long** (auto-generated)
- Added `partyId` field (from XML) separate from database ID
- Added `color` field for map visualization
- Party now belongs to a specific Election (many-to-one relationship)

---

## Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           ELECTION (Root Entity)                         │
│ ─────────────────────────────────────────────────────────────────────── │
│  PK: id (String)                                                         │
│  name, date                                                              │
└─────────────────────────────────────────────────────────────────────────┘
     │
     ├─────────────────┬─────────────────┬──────────────────┬──────────────
     │                 │                 │                  │
     ▼                 ▼                 ▼                  ▼
┌──────────┐   ┌──────────────┐   ┌─────────────┐   ┌──────────────────┐
│  PARTY   │   │ CONSTITUENCY │   │ CANDIDATE   │   │  PARTY_RESULT    │
├──────────┤   ├──────────────┤   ├─────────────┤   ├──────────────────┤
│PK: id    │   │PK: id (Str)  │   │PK: id       │   │PK: id            │
│partyId   │   │name          │   │name         │   │FK: election_id   │
│name      │   │FK: election  │   │FK: election │   │FK: party_id      │
│shortcode │   │              │   │FK: party_id │   │totalVotes        │
│color     │   │              │   │position     │   │percentage        │
│FK: elect │   │              │   │              │   │seats             │
└──────────┘   └──────────────┘   └─────────────┘   │elected           │
                     │                                └──────────────────┘
                     │ 1:N                            
                     ▼                                
              ┌──────────────────┐                   ┌──────────────────────┐
              │  MUNICIPALITY    │───────────────────│ MUNICIPALITY_RESULT  │
              ├──────────────────┤        1:N        ├──────────────────────┤
              │PK: id (String)   │                   │PK: id                │
              │name              │                   │FK: election_id       │
              │FK: constituency  │                   │FK: municipality_id   │
              └──────────────────┘                   │FK: party_id          │
                     │                                │totalVotes            │
                     │ 1:N                            │percentage            │
                     ▼                                └──────────────────────┘
              ┌──────────────────┐
              │ POLLING_BUREAU   │                   ┌──────────────────────────┐
              ├──────────────────┤───────────────────│ POLLING_BUREAU_RESULT    │
              │PK: id (Long)     │        1:N        ├──────────────────────────┤
              │name              │                   │PK: id                    │
              │externalId        │                   │FK: election_id           │
              │FK: municipality  │                   │FK: polling_bureau_id     │
              └──────────────────┘                   │FK: party_id              │
                                                      │totalVotes                │
                                                      │percentage                │
                                                      └──────────────────────────┘

Legend:
  PK = Primary Key
  FK = Foreign Key
  1:N = One-to-Many relationship
```

---

## Database Tables

### Geographic Entities

#### `constituency`
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | String | Constituency code (e.g., "HSB9") |
| `name` | String | Constituency name |
| `election_id` (FK) | String | References `election.election_id` |

#### `municipality`
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | String | 4-digit municipality code (e.g., "0363") |
| `name` | String | Municipality name (e.g., "Amsterdam") |
| `constituency_id` (FK) | String | References `constituency.id` |

#### `polling_bureau`
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | Long | Auto-generated ID |
| `name` | String | Polling bureau name |
| `external_id` | String | Full ID from XML (e.g., "0363::SB1") |
| `municipality_id` (FK) | String | References `municipality.id` |

### Result Entities

#### `party_result` (National Level)
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | Long | Auto-generated ID |
| `election_id` (FK) | String | References `election.election_id` |
| `party_id` (FK) | Long | References `party.id` |
| `total_votes` | Integer | Total votes nationally |
| `percentage` | Double | Vote percentage |
| `seats` | Integer | Seats won |
| `elected` | Boolean | Whether party was elected |
| **UNIQUE** | - | `(election_id, party_id)` |

#### `municipality_result`
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | Long | Auto-generated ID |
| `election_id` (FK) | String | References `election.election_id` |
| `municipality_id` (FK) | String | References `municipality.id` |
| `party_id` (FK) | Long | References `party.id` |
| `total_votes` | Integer | Votes in this municipality |
| `percentage` | Double | Vote percentage in municipality |
| **UNIQUE** | - | `(election_id, municipality_id, party_id)` |

#### `polling_bureau_result`
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | Long | Auto-generated ID |
| `election_id` (FK) | String | References `election.election_id` |
| `polling_bureau_id` (FK) | Long | References `polling_bureau.id` |
| `party_id` (FK) | Long | References `party.id` |
| `total_votes` | Integer | Votes at this polling bureau |
| `percentage` | Double | Vote percentage at bureau |
| **UNIQUE** | - | `(election_id, polling_bureau_id, party_id)` |

### Updated Entities

#### `party` (Modified)
| Column | Type | Description |
|--------|------|-------------|
| `id` (PK) | Long | **CHANGED: Now Long (was String)** |
| `party_id` | Long | Original party ID from XML |
| `name` | String | Party name |
| `shortcode` | String | Party abbreviation |
| `color` | String | **NEW: Hex color for visualization** |
| `election_id` (FK) | String | References `election.election_id` |
| **UNIQUE** | - | `(party_id, election_id)` |

---

## How to Use the New Structure

### 1. **Querying Election Results by Municipality**
```java
// Get all party results for Amsterdam municipality
MunicipalityResultRepository.findByElectionAndMunicipality(election, municipality);

// Get winning party in a municipality
MunicipalityResultRepository.findTopByElectionAndMunicipalityOrderByTotalVotesDesc(election, municipality);
```

### 2. **Geographic Hierarchy Navigation**
```java
// Navigate from Election → Constituencies → Municipalities → Polling Bureaus
Election election = electionService.findById("TK2023");
List<Constituency> constituencies = election.getConstituencies();
for (Constituency c : constituencies) {
    List<Municipality> municipalities = c.getMunicipalities();
    for (Municipality m : municipalities) {
        List<PollingBureau> bureaus = m.getPollingBureaus();
    }
}
```

### 3. **Accessing Party Colors for Map Visualization**
```java
// Frontend endpoint to get municipality colors
@GetMapping("/municipalities/{municipalityId}/winning-color")
public ResponseEntity<String> getWinningColor(@PathVariable String municipalityId) {
    MunicipalityResult winner = municipalityResultRepository
        .findTopByMunicipalityOrderByTotalVotesDesc(municipalityId);
    return ResponseEntity.ok(winner.getParty().getColor());
}
```

### 4. **XML Parser Usage**
The transformers automatically create the entire hierarchy:
```java
// DutchConstituencyVotesTransformer now creates:
// 1. Constituency entities
// 2. Municipality entities (filtered to 4-digit codes only)
// 3. PollingBureau entities (extracted from format "0363::SB1")
// 4. MunicipalityResult and PollingBureauResult for vote data
```

---

## Important Notes

### Breaking Changes
1. **Party ID type changed from String to Long** - update any code that references `Party.id`
2. **Municipality is now independent** - no longer a child of Election directly
3. **Constituency IDs normalized** - all use "HSB" prefix (e.g., "HSB9" not "9")

### Data Integrity
- **No duplicate municipalities** - filtering ensures only 4-digit municipality codes (e.g., "0363" ✅, "0363::SB1" ❌)
- **No duplicate polling bureaus** - checked by `externalId` before insertion
- **Unique constraints prevent duplicate results** - composite keys on result entities

### Performance Considerations
- All relationships use `FetchType.LAZY` to avoid N+1 queries
- Use `@JsonIgnore` on parent relationships to prevent circular references
- Result entities are indexed by their unique constraints for fast lookups

---

## Migration from Old Structure

If you have existing data, you'll need to:

1. **Backup your database** - structure changes are not backward compatible
2. **Clear H2 database** - set `spring.jpa.hibernate.ddl-auto=create-drop` in `application.properties`
3. **Re-import XML data** - the transformers will rebuild the entire structure correctly
4. **Update API clients** - Party IDs are now Long, not String

---

## API Endpoints

New endpoints for geographic data:

```
GET /api/constituency/{id}                    - Get constituency by ID
GET /api/constituency/{id}/municipalities      - Get municipalities in constituency
GET /api/municipality/{id}                     - Get municipality by ID
GET /api/municipality/name/{name}              - Get municipality by name
GET /api/municipality/{id}/polling-bureaus     - Get polling bureaus in municipality
GET /api/election/{id}/municipality-results    - Get all municipality results for election
```

---

## Frontend Integration

The new structure enables the **election result map** feature:
- Each municipality colored by winning party
- Click municipality to see detailed vote breakdown
- Drill down to polling bureau level results
- Legend shows party colors (from `party.color` field)

---

## Summary

This refactoring provides:
- Clear separation between geographic entities and result data  
- Efficient querying at any geographic level  
- Support for detailed polling bureau data  
- Prevention of data duplication via unique constraints  
- Map visualization with party colors  
- Scalable structure for future elections  

The new structure follows **normalization principles** and supports both **aggregated national views** and **granular local analysis**.
