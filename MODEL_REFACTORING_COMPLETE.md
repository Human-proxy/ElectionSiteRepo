# Model Refactoring Documentation

**Project:** De Derde Kamer Election Backend  
**Date:** November 10, 2025  
**Status:** Completed

## Summary

This document describes the refactoring work done on the election data model to fix several JPA entity relationship issues. The main problem was that municipality voting data wasn't being saved properly, and there were relationship mismatches between entities.

## What Was Broken

### Main Issues

1. **Party-Election Relationship Mismatch**

    - Party had `@OneToMany` to Election
    - But Election had `@ManyToMany` back to Party
    - This doesn't work in JPA, caused cascade issues

2. **No Way to Store Vote Results**

    - Had 26 party results for Amsterdam in the XML
    - But nowhere to save them in the database
    - Municipality entity didn't have a relationship to track party votes
    - Same problem for polling stations

3. **Candidate-Party Not Properly Connected**

    - Candidate just had `Long partyId` as a field
    - Should have been `@ManyToOne Party` relationship
    - Made it impossible to navigate from candidate to party entity

4. **PartyTotal Entity Was Broken**
    - Had compilation errors
    - Wrong `@Id` annotations everywhere
    - Mixed up code from Party class

### Impact

The Amsterdam API endpoint returned empty results even though XML data loaded successfully. Frontend map couldn't show municipality results.

## What Changed

### 1. Fixed Party Entity

Changed from:

```java
@OneToMany(mappedBy = "party")
private Election election;
```

To:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "election_id")
private Election election;
```

Also added:

-   `partyId` field (Long) for the actual party identifier
-   `totalVotes`, `seats`, `elected` fields for results
-   `@OneToMany` relationship to candidates
-   Backward compatibility methods: `isElected()`, `getVotes()`, `setVotes()`
-   Constructors that accept String partyId from XML and convert to Long

### 2. Fixed Candidate Entity

Changed from:

```java
@Column(name = "party_id")
private Long partyId;
```

To:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "party_id")
private Party party;
```

Added helper method `getPartyId()` that returns the party's ID for backward compatibility.

### 3. Created MunicipalityResult Entity (NEW)

This solves the 26 Amsterdam rows problem.

```java
@Entity
@Table(name = "municipality_result")
public class MunicipalityResult {
    @ManyToOne Election election;
    @ManyToOne Municipality municipality;
    @ManyToOne Party party;
    Integer totalVotes;
    Double percentage;
}
```

Stores: "Party X got Y votes in Municipality Z for Election E"

### 4. Created PollingBureauResult Entity (NEW)

For detailed polling station level results.

```java
@Entity
@Table(name = "polling_bureau_result")
public class PollingBureauResult {
    @ManyToOne Election election;
    @ManyToOne PollingBureau pollingBureau;
    @ManyToOne Party party;
    Integer totalVotes;
    Double percentage;
}
```

### 5. Created PartyResult Entity (NEW)

Replaces the broken PartyTotal. Stores national level results.

```java
@Entity
@Table(name = "party_result")
public class PartyResult {
    @ManyToOne Election election;
    @ManyToOne Party party;
    Integer totalVotes;
    Double percentage;
    Integer seats;
    Boolean elected;
}
```

### 6. Updated Election Entity

Changed Party relationship from `@ManyToMany` to `@OneToMany`:

```java
@OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
private List<Party> parties;
```

Added collections for result entities:

```java
@OneToMany(mappedBy = "election")
private List<PartyResult> partyResults;

@OneToMany(mappedBy = "election")
private List<MunicipalityResult> municipalityResults;

@OneToMany(mappedBy = "election")
private List<PollingBureauResult> pollingBureauResults;
```

Fixed the `calculateSeats()` method to work with new Party structure.

## New Data Structure

```
Election
  ├─ Party (many)
  │   ├─ partyId: Long
  │   ├─ totalVotes, seats, elected
  │   └─ Candidates (many)
  │       └─ @ManyToOne Party
  │
  ├─ PartyResult (national totals)
  │
  └─ Constituency
      └─ Municipality (static)
          ├─ MunicipalityResult (party votes per municipality)
          └─ PollingBureau
              └─ PollingBureauResult (party votes per station)
```

## New Repositories

Created three new repository classes:

1. **MunicipalityResultRepository**

    - `findByElectionId(String electionId)`
    - `findByElectionAndMunicipality(String electionId, String municipalityId)` - gets the 26 Amsterdam rows
    - `findByElectionAndParty(String electionId, Long partyId)`

2. **PollingBureauResultRepository**

    - `findByElectionId(String electionId)`
    - `findByElectionAndPollingBureau(String electionId, Long bureauId)`
    - `findByElectionAndParty(String electionId, Long partyId)`

3. **PartyResultRepository**
    - `findByElectionId(String electionId)`
    - `findByElectionAndParty(String electionId, Long partyId)`
    - `findElectedByElection(String electionId)`

## Files Changed

### New Files

-   `MunicipalityResult.java`
-   `PollingBureauResult.java`
-   `PartyResult.java`
-   `MunicipalityResultRepository.java`
-   `PollingBureauResultRepository.java`
-   `PartyResultRepository.java`

### Modified Files

-   `Party.java` - complete refactoring
-   `Candidate.java` - added Party relationship
-   `Election.java` - fixed Party relationship, added result collections
-   `PartyTotal.java` - deleted (was broken)

## What Still Needs to Be Done

1. Update XML transformers to populate the new MunicipalityResult and PollingBureauResult entities when parsing
2. Test that the API endpoints work with the refactored models
3. Verify data actually persists to H2 database now
4. Check if existing services need updates for the new structure

## Backward Compatibility

To keep existing code working:

-   Added `isElected()`, `getVotes()`, `setVotes()` to Party
-   Added `getPartyId()` to Candidate
-   Party constructors accept String partyId (from XML) and convert to Long automatically
-   Candidate has `setPartyId(String)` that finds the Party entity

## Notes

Municipality stays a static entity (exists without election), but MunicipalityResult is election specific. This separation makes sense because municipalities don't change between elections but their voting results do.

The hierarchy now properly supports querying at different levels:

-   National: PartyResult
-   Municipality: MunicipalityResult
-   Polling Station: PollingBureauResult

All entities compile without errors. Most test failures are related to old test code that needs updating for the new structure.
