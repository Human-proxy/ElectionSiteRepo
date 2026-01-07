package nl.hva.dederdekamer.election_backend.XMLParser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import nl.hva.dederdekamer.election_backend.XMLParser.exception.ElectionProcessingException;
import nl.hva.dederdekamer.election_backend.XMLParser.factory.DutchElectionParserFactory;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.PathUtils;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.DutchElectionParser;
import nl.hva.dederdekamer.election_backend.repository.ElectionRepository;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import static nl.hva.dederdekamer.election_backend.config.CacheConfig.ELECTION_CACHE;

/**
 * A demo service for demonstrating how an EML-XML parser can be used inside a backend application.<br/>
 * <br/>
 * This service is responsible for orchestrating the parsing of election data and handling the results.
 * Results are cached in memory to avoid re-parsing on every request.
 */
@Service
public class DutchElectionService {

    private static final Logger logger = LoggerFactory.getLogger(DutchElectionService.class);
    
    private final DutchElectionParserFactory parserFactory;

    private final ElectionPersistenceService electionPersistenceService;

    private final ElectionRepository electionRepository;

    // Cache to store parsed elections (key: "electionId:folderName")
    private final Map<String, Election> electionCache = new ConcurrentHashMap<>();

    private static final Pattern ELECTION_ID_PATTERN = Pattern.compile(".*_(TK\\d{4}).*\\.xml", Pattern.CASE_INSENSITIVE);

    /**
     * Constructor for dependency injection of the parser factory and persistence service.
     * 
     * @param parserFactory The factory used to create parser instances
     * @param electionPersistenceService The service used to persist elections
     * @param electionRepository The repository for election database operations
     */
    public DutchElectionService(DutchElectionParserFactory parserFactory,
                                ElectionPersistenceService electionPersistenceService,
                                ElectionRepository electionRepository) {
        this.parserFactory = parserFactory;
        this.electionPersistenceService = electionPersistenceService;
        this.electionRepository = electionRepository;
    }

    public Election readResults(String electionId, String folderName) {
        // Create cache key from electionId and folderName
        String cacheKey = electionId + ":" + folderName;
        
        // Check if already parsed and cached
        if (electionCache.containsKey(cacheKey)) {
            return electionCache.get(cacheKey);
        }


        // Before attempting to parse the single requested election, scan the folder for any elections and parse all of them.
        try {
            parseAllElectionsInFolder(folderName);
        } catch (Exception e) {
            logger.error("Error while parsing elections in folder '{}': {}", folderName, e.getMessage(), e);
            throw new ElectionProcessingException("Unable to parse elections from folder", e, electionId, folderName);
        }

        // After parsing all elections in folder, try to return requested election from cache
        if (electionCache.containsKey(cacheKey)) {
            return electionCache.get(cacheKey);
        }

        // If still not present, fall back to existing behavior: attempt to parse only the requested election files
        Election election = new Election(electionId);
        // Using factory pattern with dependency injection - much cleaner!
        DutchElectionParser electionParser = parserFactory.createParser(election);

        try {
            logger.info("Starting to process election '{}' from folder '{}'", electionId, folderName);

            // Assuming the election data is somewhere on the class-path it should be found.
            // Please note that you can also specify an absolute path to the folder!
            electionParser.parseResults(electionId, PathUtils.getResourcePath("/%s".formatted(folderName)));

            logger.info("Successfully processed election '{}' from folder '{}'", electionId, folderName);

            // Calculate seats per party based on elected candidates
            election.calculateSeats();
            // Calculate percentages for all result entities
            election.calculatePercentages();
            electionCache.put(cacheKey, election);
            electionPersistenceService.saveElection(election);
            return election;
        } catch (IOException e) {
            logger.error("IO error while processing election '{}' from folder '{}': {}", 
                        electionId, folderName, e.getMessage(), e);
            throw new ElectionProcessingException("Unable to read election data files", e, electionId, folderName);

        } catch (XMLStreamException | ParserConfigurationException | SAXException e) {
            logger.error("XML parsing error while processing election '{}' from folder '{}': {}", 
                        electionId, folderName, e.getMessage(), e);
            throw new ElectionProcessingException("Invalid or corrupted XML election data", e, electionId, folderName);

        } catch (NullPointerException e) {
            logger.error("Null pointer error while processing election '{}' from folder '{}': {}", 
                        electionId, folderName, e.getMessage(), e);
            throw new ElectionProcessingException("Missing required election data or invalid folder path", e, electionId, folderName);

        } catch (Exception e) {
            logger.error("Unexpected error while processing election '{}' from folder '{}': {}", 
                        electionId, folderName, e.getMessage(), e);
            throw new ElectionProcessingException("Unexpected error during election processing", e, electionId, folderName);
        }
    }

    /**
     * Scan the folder and parse all XML files found. Files are grouped by election short code (e.g. TK2021).
     * Each group will be turned into its own Election instance, cached and persisted.
     */
    private void parseAllElectionsInFolder(String folderName) throws IOException, ParserConfigurationException, SAXException {
        String folderPath = PathUtils.getResourcePath("/%s".formatted(folderName));
        if (folderPath == null) {
            logger.warn("Resource path for folder '{}' could not be resolved.", folderName);
            return;
        }

        logger.info("Scanning folder '{}' for election XML files...", folderPath);

        // Collect all XML files in folder tree
        List<Path> xmlFiles = Files.walk(Path.of(folderPath))
                .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".xml"))
                .collect(Collectors.toList());

        // Group files by election id extracted from filename
        Map<String, List<Path>> filesByElectionId = new HashMap<>();
        for (Path p : xmlFiles) {
            String fname = p.getFileName().toString();
            Matcher m = ELECTION_ID_PATTERN.matcher(fname);
            if (m.matches()) {
                String eid = m.group(1).toUpperCase();
                filesByElectionId.computeIfAbsent(eid, k -> new ArrayList<>()).add(p);
            } else {
            }
        }

        // For each election id, parse the files into a dedicated Election instance
        for (Map.Entry<String, List<Path>> entry : filesByElectionId.entrySet()) {
            String eid = entry.getKey();
            List<Path> files = entry.getValue();
            String cacheKey = eid + ":" + folderName;
            if (electionCache.containsKey(cacheKey)) {
                continue;
            }

            Election election = new Election(eid);
            DutchElectionParser parser = parserFactory.createParser(election);

            logger.info("Parsing {} files for election {}", files.size(), eid);
            for (Path file : files.stream().sorted().collect(Collectors.toList())) {
                try {
                    parser.parseSingleFile(file);
                } catch (IOException | ParserConfigurationException | SAXException ex) {
                    logger.warn("Failed to parse file {} for election {}: {}", file, eid, ex.getMessage());
                }
            }

            // Post-processing and caching/persistence
            election.calculateSeats();
            election.calculatePercentages();
            electionCache.put(cacheKey, election);
            electionPersistenceService.saveElection(election);
            logger.info("Parsed and persisted election {} with {} parties and {} elected candidates", eid, election.getParties().size(), election.getElectedCandidates().size());
        }
    }

    /**
     * Clear the election cache. Useful for development/testing when you want to force a re-parse.
     */
    public void clearCache() {
        logger.info("Clearing election cache (was holding {} elections)", electionCache.size());
        electionCache.clear();
    }
    
    /**
     * Get election by ID from the database.
     * Results are cached to avoid repeated database queries.
     *
     * @param electionId the election ID
     * @return optional election
     */
    @Cacheable(value = ELECTION_CACHE, key = "'election-' + #electionId")
    public Optional<Election> getElectionById(String electionId) {
        return electionRepository.findById(electionId);
    }


}
