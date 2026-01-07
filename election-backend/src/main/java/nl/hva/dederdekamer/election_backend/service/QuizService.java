package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.MunicipalityResult;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.XMLParser.model.PartyResult;
import nl.hva.dederdekamer.election_backend.dto.QuizOptionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizQuestionDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizRequestDTO;
import nl.hva.dederdekamer.election_backend.dto.QuizResponseDTO;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityResultRepository;
import nl.hva.dederdekamer.election_backend.repository.PartyRepository;
import nl.hva.dederdekamer.election_backend.repository.PartyResultRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service that builds the quiz questions and processes quiz answers
 * into readable election results.
 *
 * The quiz allows a user to:
 * - choose an election year
 * - choose a political party
 * - choose a region (national or municipality-level)
 * - choose the type of data (seats, votes or percentage)
 *
 * Based on the selection, this service queries the repositories and
 * returns a formatted {@link QuizResponseDTO} including a narrative text.
 */
@Service
public class QuizService {

    private final PartyResultRepository partyResultRepository;
    private final MunicipalityResultRepository municipalityResultRepository;
    private final PartyService partyService;
    private final MunicipalityService municipalityService;
    private final PartyRepository partyRepository;

    /**
     * Constructs a new {@code QuizService} with the required dependencies.
     *
     * @param partyResultRepository repository for national party results
     * @param municipalityResultRepository repository for municipality results
     * @param partyService domain service for parties
     * @param municipalityService domain service for municipalities
     * @param partyRepository repository for party entities
     */
    public QuizService(PartyResultRepository partyResultRepository,
                       MunicipalityResultRepository municipalityResultRepository,
                       PartyService partyService,
                       MunicipalityService municipalityService,
                       PartyRepository partyRepository) {
        this.partyResultRepository = partyResultRepository;
        this.municipalityResultRepository = municipalityResultRepository;
        this.partyService = partyService;
        this.municipalityService = municipalityService;
        this.partyRepository = partyRepository;
    }

    /**
     * Builds the list of quiz questions for a given year.
     *
     * The quiz currently consists of four questions:
     * q1: election year
     * q2: political party
     * q3: region (Nederland or municipality)
     * q4: data type (seats, votes or percentage)
     *
     * @param year the election year as string (for example "2023")
     * @return a list of quiz questions including their options
     */
    public List<QuizQuestionDTO> getQuestions(String year) {
        List<QuizQuestionDTO> questions = new ArrayList<>();
        String electionId = "TK" + year;

        // Q1: Year
        List<QuizOptionDTO> yearOptions = List.of(
                new QuizOptionDTO("2023", "2023"),
                new QuizOptionDTO("2021", "2021"),
                new QuizOptionDTO("2017", "2017")
        );
        questions.add(new QuizQuestionDTO("q1", "In welk jaar wilt u de quiz doen?", "SELECT", yearOptions));

        // Q2: Party (Fetch elected parties for specific year)
        List<Party> parties = partyService.getElectedPartiesByElection(electionId);
        List<QuizOptionDTO> partyOptions = parties.stream()
                // We must use the unique Database ID, not the list number
                .map(p -> new QuizOptionDTO(p.getId(), p.getName()))
                .collect(Collectors.toList());

        questions.add(new QuizQuestionDTO("q2", "Welke partij interesseert je?", "SEARCHABLE_SELECT", partyOptions));

        // Q3: Region
        List<Object> municipalities = municipalityService.getAllMunicipalitiesForQuiz(electionId);
        List<QuizOptionDTO> regionOptions = new ArrayList<>();
        regionOptions.add(new QuizOptionDTO("Nederland", "Nederland (Landelijk)"));

        for (Object m : municipalities) {
            if (m instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) m;
                String name = (String) map.get("name");
                regionOptions.add(new QuizOptionDTO(name, name));
            }
        }
        questions.add(new QuizQuestionDTO("q3", "Welke regio wil je zien?", "SEARCHABLE_SELECT", regionOptions));

        // Q4: Data Type
        List<QuizOptionDTO> typeOptions = List.of(
            new QuizOptionDTO("SEATS", "Aantal zetels", "Hoeveel stoelen elke partij heeft in de Kamer"),
            new QuizOptionDTO("VOTES", "Aantal stemmen", "Hoeveel mensen er echt op elke partij stemden"),
            new QuizOptionDTO("PERCENTAGE", "Percentages", "Percentage van alle uitgebrachte stemmen")
        );
        questions.add(new QuizQuestionDTO("q4", "Wat wil je zien?", "CARDS", typeOptions));

        return questions;
    }

    /**
     * Processes a completed quiz and returns a formatted result.
     *
     * The method:
     * - resolves the selected party by ID to obtain its name
     * - routes to a national or local result handler based on the region
     * - handles three data types: SEATS, VOTES and PERCENTAGE
     * - falls back to national data if local data is missing
     *
     * @param request the user's completed quiz request
     * @return a quiz response containing labels, formatted values and narrative text
     * @throws ResourceNotFoundException if the selected party or data cannot be found
     */
    public QuizResponseDTO processQuiz(QuizRequestDTO request) {
        String electionId = "TK" + request.getYear();

        // 1. Resolve party name from the selected party ID
        Party referenceParty = partyRepository.findById(request.getPartyId())
                .orElseThrow(() -> new ResourceNotFoundException("Party ID not found"));
        String targetPartyName = referenceParty.getName();

        // 2. Handle SEATS (always national)
        if ("SEATS".equalsIgnoreCase(request.getDataType())) {
            return getNationalSeatsByName(electionId, targetPartyName);
        }

        // 3. Handle regional results if a specific municipality is selected
        if (request.getRegion() != null
                && !request.getRegion().isBlank()
                && !request.getRegion().equalsIgnoreCase("Nederland")) {

            try {
                return getMunicipalityResultByName(electionId, targetPartyName, request.getRegion(), request.getDataType());
            } catch (ResourceNotFoundException e) {
                // Fallback to national data when local data is missing
                QuizResponseDTO nationalFallback = getNationalResultByName(electionId, targetPartyName, request.getDataType());
                nationalFallback.setNarrative("Lokale resultaten ontbreken voor " + request.getRegion() + ". " + nationalFallback.getNarrative());
                nationalFallback.setRegionName(request.getRegion() + " (Data niet beschikbaar)");
                return nationalFallback;
            }
        }

        // 4. Default: handle national results
        return getNationalResultByName(electionId, targetPartyName, request.getDataType());
    }

    /**
     * Returns the national seat count for a party in a given election.
     *
     * This method:
     * - looks up {@link PartyResult} rows for the given election
     * - matches the correct party using    y name matching
     * - reads the seat count from the {@link Party} entity
     * - builds a {@link QuizResponseDTO} with a Dutch narrative sentence
     *
     * @param electionId the election identifier (for example "TK2023")
     * @param partyName the name of the party to match
     * @return a quiz response containing the seat count
     * @throws ResourceNotFoundException if no seat data was found for the party
     */
    private QuizResponseDTO getNationalSeatsByName(String electionId, String partyName) {
        List<PartyResult> results = partyResultRepository.findByElectionId(electionId);

        PartyResult result = results.stream()
                .filter(pr -> fuzzyMatch(pr.getParty().getName(), partyName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Geen zetels gevonden voor " + partyName + " in " + electionId));

        // Use the seat count from the Party entity, not the PartyResult entity
        int seats = result.getParty().getSeats();

        String value = seats + " zetels";
        String narrative = String.format("In %s behaalde %s %d zetels in de Tweede Kamer.",
                electionId.replace("TK", ""), result.getParty().getName(), seats);

        return new QuizResponseDTO(result.getParty().getName(), "Nederland", "Aantal Zetels", value, narrative);
    }

    /**
     * Returns the result for a party in a specific municipality.
     *
     * Depending on the data type, this method returns either:
     * - the percentage of votes
     * - the total number of votes
     *
     * @param electionId the election identifier (for example "TK2023")
     * @param partyName the name of the party to match
     * @param municipalityName the name of the municipality
     * @param dataType the requested data type, such as "PERCENTAGE" or "VOTES"
     * @return a quiz response containing municipality-level results
     * @throws ResourceNotFoundException if no local result was found for the party
     */
    private QuizResponseDTO getMunicipalityResultByName(String electionId,
                                                        String partyName,
                                                        String municipalityName,
                                                        String dataType) {
        List<MunicipalityResult> results =
                municipalityResultRepository.findByElectionAndMunicipalityName(electionId, municipalityName);

        MunicipalityResult result = results.stream()
                .filter(r -> fuzzyMatch(r.getParty().getName(), partyName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Lokale resultaten", partyName));

        String value;
        String narrative;
        String label;
        Locale dutchLocale = Locale.GERMANY;

        if ("PERCENTAGE".equalsIgnoreCase(dataType)) {
            value = String.format("%.1f%%", result.getPercentage());
            label = "Percentage";
            narrative = String.format("In %s behaalde %s %s van de stemmen in %s.",
                    electionId.replace("TK", ""), result.getParty().getName(), value, municipalityName);
        } else {
            value = NumberFormat.getNumberInstance(dutchLocale).format(result.getTotalVotes());
            label = "Aantal Stemmen";
            narrative = String.format("In %s kreeg %s in totaal %s stemmen in %s.",
                    electionId.replace("TK", ""), result.getParty().getName(), value, municipalityName);
        }

        return new QuizResponseDTO(result.getParty().getName(), municipalityName, label, value, narrative);
    }

    /**
     * Returns the national result (votes or percentage) for a party.
     *
     * Depending on the data type, this method returns:
     * - the national percentage of votes
     * - the national total number of votes
     *
     * @param electionId the election identifier (for example "TK2023")
     * @param partyName the name of the party to match
     * @param dataType the requested data type, such as "PERCENTAGE" or "VOTES"
     * @return a quiz response containing national-level results
     * @throws ResourceNotFoundException if no national result was found for the party
     */
    private QuizResponseDTO getNationalResultByName(String electionId,
                                                    String partyName,
                                                    String dataType) {
        List<PartyResult> results = partyResultRepository.findByElectionId(electionId);

        PartyResult result = results.stream()
                .filter(pr -> fuzzyMatch(pr.getParty().getName(), partyName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Landelijke resultaten", partyName));

        String value;
        String narrative;
        String label;
        Locale dutchLocale = Locale.GERMANY;

        if ("PERCENTAGE".equalsIgnoreCase(dataType)) {
            double pct = result.getPercentage() != null ? result.getPercentage() : 0.0;
            value = String.format("%.1f%%", pct);
            label = "Landelijk Percentage";
            narrative = String.format("Landelijk behaalde %s %s van de totale stemmen.",
                    result.getParty().getName(), value);
        } else {
            value = NumberFormat.getNumberInstance(dutchLocale).format(result.getTotalVotes());
            label = "Landelijk Aantal Stemmen";
            narrative = String.format("Landelijk kreeg %s in totaal %s stemmen.",
                    result.getParty().getName(), value);
        }

        return new QuizResponseDTO(result.getParty().getName(), "Nederland", label, value, narrative);
    }

    /**
     * Helper method to match party names in a tolerant way.
     *
     * This is used to handle:
     * - differences in casing and punctuation
     * - merged party names such as GroenLinks/PvdA
     * - partial name matches
     *
     * @param dbName the party name stored in the database
     * @param targetName the party name being searched for
     * @return true if the names are considered a match, false otherwise
     */
    private boolean fuzzyMatch(String dbName, String targetName) {
        if (dbName == null || targetName == null) return false;

        String dbClean = normalize(dbName);
        String targetClean = normalize(targetName);

        // 1. Exact match after cleanup
        if (dbClean.equals(targetClean)) return true;

        // 2. Handle the specific GroenLinks / PvdA merger (2023)
        boolean isGlPvdACombined = dbClean.contains("groenlinks") && dbClean.contains("pvda");
        boolean targetIsLeft = targetClean.contains("pvda")
                || targetClean.contains("groenlinks")
                || targetClean.contains("partij van de arbeid");

        if (isGlPvdACombined && targetIsLeft) {
            return true;
        }

        // 3. Standard containment (e.g. "VVD" is inside a longer name)
        return dbClean.contains(targetClean) || targetClean.contains(dbClean);
    }

    /**
     * Normalizes a party name by removing punctuation, casing and extra spaces.
     *
     * Example:
     * "Partij van de Arbeid (P.v.d.A.)" becomes "partij van de arbeid pvda".
     *
     * @param s the input string to normalize
     * @return a cleaned, lowercased version of the input string
     */
    private String normalize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replace(".", "")        // Remove dots (P.v.d.A -> pvda)
                .replace("(", "")        // Remove brackets
                .replace(")", "")
                .replace("/", "")        // Remove slashes
                .replace("-", " ")       // Replace dashes with space
                .replaceAll("\\s+", " ") // Collapse multiple spaces
                .trim();
    }
    /**
     * Generates a CSV file content based on the user's quiz request.
     * 
     * Steps:
     * 1. Calculates the result using {@link #processQuiz(QuizRequestDTO)}.
     * 2. Formats the data into a CSV string with a header and a data row.
     * 
     *
     * @param request The user's quiz answers.
     * @return A byte array containing the CSV data.
     */
    public byte[] generateExportCsv(QuizRequestDTO request) {
        // 1. Get the result data
        QuizResponseDTO result = processQuiz(request);

        // 2. Build CSV Content
        StringBuilder csv = new StringBuilder();
        
        // CSV Header
        csv.append("Verkiezingsjaar,Partij,Regio,Onderwerp,Waarde,Omschrijving\n");

        // CSV Row (Escape special chars)
        csv.append(escapeCsv(request.getYear())).append(",");
        csv.append(escapeCsv(result.getPartyName())).append(",");
        csv.append(escapeCsv(result.getRegionName())).append(",");
        csv.append(escapeCsv(result.getMetricLabel())).append(",");
        csv.append(escapeCsv(result.getFormattedValue())).append(",");
        csv.append(escapeCsv(result.getNarrative())).append("\n");

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Helper method to escape CSV fields.
     * Wraps text in quotes if it contains commas or quotes.
     */
    private String escapeCsv(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
