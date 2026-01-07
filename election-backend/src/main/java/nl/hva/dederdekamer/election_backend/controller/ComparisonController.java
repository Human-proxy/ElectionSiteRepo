package nl.hva.dederdekamer.election_backend.controller;

import nl.hva.dederdekamer.election_backend.repository.CandidateRepository;
import nl.hva.dederdekamer.election_backend.dto.CompareResponse;
import nl.hva.dederdekamer.election_backend.dto.PartySeatsComparison;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("api")
public class ComparisonController {

    private final CandidateRepository electedCandidateRepository;

    public ComparisonController(CandidateRepository electedCandidateRepository) {
        this.electedCandidateRepository = electedCandidateRepository;
    }

    /**
     * Compare two elections (by year) and return per-party seat counts for both years.
     * Defaults to 2023 for both parameters if not provided.
     *
     * Example: GET /api/elections/compare?year1=2021&year2=2023
     */
    @GetMapping("elections/compare")
    public ResponseEntity<CompareResponse> compareElections(
            @RequestParam(name = "year1", required = false, defaultValue = "2017") int year1,
            @RequestParam(name = "year2", required = false, defaultValue = "2023") int year2
    ) {
        // Map requested years to election ids stored in DB (election.election_id)
        final String electionId1;
        final String electionId2;
        try {
            electionId1 = mapYearToElectionId(year1);
            electionId2 = mapYearToElectionId(year2);
        } catch (ResponseStatusException ex) {
            // Ensure a clean 400 response in tests/environments where exception translation differs
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            throw ex; // propagate others
        }

        // Aggregate seats by canonicalized party name (not partyId) to match parties across years
        Map<String, Integer> seatsByKeyYear1 = new HashMap<>();
        Map<String, Integer> seatsByKeyYear2 = new HashMap<>();
        Map<String, String> displayNameByKey = new HashMap<>(); // pretty name per canonical key

        List<Object[]> data1 = Optional.ofNullable(
                electedCandidateRepository.findSeatCountsByElection(electionId1)
        ).orElse(Collections.emptyList());

        List<Object[]> data2 = Optional.ofNullable(
                electedCandidateRepository.findSeatCountsByElection(electionId2)
        ).orElse(Collections.emptyList());

        data1.forEach(row -> {
            if (row == null || row.length < 3) return;  // safe check
            String rawName = (String) row[1];
            if (rawName == null || rawName.isBlank()) return;
            int seats = ((Number) row[2]).intValue(); // safe cast van Long/Integer
            String key = canonicalPartyKey(rawName);
            seatsByKeyYear1.merge(key, seats, Integer::sum);
            displayNameByKey.putIfAbsent(key, canonicalDisplayName(key, rawName));
        });

        data2.forEach(row -> {
            if (row == null || row.length < 3) return;  // safe check
            String rawName = (String) row[1];
            if (rawName == null || rawName.isBlank()) return;
            int seats = ((Number) row[2]).intValue(); // safe cast
            String key = canonicalPartyKey(rawName);
            seatsByKeyYear2.merge(key, seats, Integer::sum);
            displayNameByKey.put(key, canonicalDisplayName(key, rawName));
        });

        if (seatsByKeyYear1.isEmpty() && seatsByKeyYear2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Union of canonical party keys across both years
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(seatsByKeyYear1.keySet());
        allKeys.addAll(seatsByKeyYear2.keySet());

        List<PartySeatsComparison> results = new ArrayList<>();
        for (String key : allKeys) {
            String name = displayNameByKey.getOrDefault(key, key);
            int s1 = seatsByKeyYear1.getOrDefault(key, 0);
            int s2 = seatsByKeyYear2.getOrDefault(key, 0);
            // Put canonical key in partyId so frontend has a stable identifier
            results.add(new PartySeatsComparison(key, name, s1, s2));
        }

        // Sort by display name for stable output
        results.sort(Comparator.comparing(PartySeatsComparison::getPartyName, String.CASE_INSENSITIVE_ORDER));

        return ResponseEntity.ok(new CompareResponse(year1, year2, results));
    }

    // Create a stable canonical key for a party across datasets (handles merged/split naming)
    private String canonicalPartyKey(String rawName) {
        String n = normalize(rawName);
        // Merge GroenLinks and PvdA into a single comparable bucket across years
        if (n.contains("groenlinks") || n.contains("partij van de arbeid")) {
            return "gl-pvda";
        }
        // Common aliases that should unify automatically (examples; expand as needed)
        if (n.equals("christenunie")) return "christenunie";
        if (n.equals("staatkundig gereformeerde partij (sgp)") || n.equals("staatkundig gereformeerde partij")) return "sgp";
        if (n.equals("partij voor de dieren")) return "pvdd";
        if (n.equals("forum voor democratie")) return "fvd";
        if (n.equals("ja21")) return "ja21";
        if (n.equals("denk")) return "denk";
        if (n.equals("volt")) return "volt";
        if (n.equals("bbb")) return "bbb";
        if (n.equals("cda")) return "cda";
        if (n.equals("d66") || n.equals("democraten 66 (d66)")) return "d66";
        if (n.equals("pvv (partij voor de vrijheid)") || n.equals("pvv")) return "pvv";
        if (n.equals("sp (socialistische partij)") || n.equals("sp")) return "sp";
        if (n.equals("50plus") || n.equals("50 plus")) return "50plus";
        // Fallback: use normalized name as-is
        return n;
    }

    // Prefer a readable display name for a canonical key (use second param as fallback)
    private String canonicalDisplayName(String key, String fallbackRaw) {
        return switch (key) {
            case "gl-pvda" -> "GroenLinks-PvdA";
            case "sgp" -> "Staatkundig Gereformeerde Partij (SGP)";
            case "pvv" -> "PVV (Partij voor de Vrijheid)";
            case "sp" -> "SP (Socialistische Partij)";
            default -> fallbackRaw;
        };
    }

    private String normalize(String s) {
        String x = s.toLowerCase(Locale.ROOT).trim();
        // collapse multiple spaces
        x = x.replaceAll("\\s+", " ");
        // unify some punctuation variants
        x = x.replace("(p.v.d.a.)", "(pvda)");
        x = x.replace("/", " / ").replaceAll("\\s+/\\s+", " / ");
        return x;
    }

    private String mapYearToElectionId(int year) {
        // Our election ids follow TKYYYY format
        if (year == 2023) return "TK2023";
        if (year == 2021) return "TK2021";
        if (year == 2017) return "TK2017";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Year " + year + " is not available. Supported years: 2017, 2021, 2023");
    }

    private YearFolder mapYearToDataset(int year) {
        // Currently we only have TK2023_HvA_UvA available in resources.
        if (year == 2023) {
            return new YearFolder("TK2023", "TK2023_HvA_UvA");
        }
        // When adding 2021/2017, extend with proper mappings here.
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Year " + year + " is not available yet. Supported years: 2023");
    }

    private record YearFolder(String electionId, String folder) {}

}
