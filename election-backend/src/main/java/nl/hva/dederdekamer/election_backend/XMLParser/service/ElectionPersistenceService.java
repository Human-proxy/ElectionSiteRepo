package nl.hva.dederdekamer.election_backend.XMLParser.service;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.repository.ElectionRepository;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityRepository;
import nl.hva.dederdekamer.election_backend.repository.ConstituencyRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for persisting Election data to the database using Hibernate.
 * Handles saving election data including parties, candidates, and results.
 */
@Service
public class ElectionPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionPersistenceService.class);

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private ConstituencyRepository constituencyRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    /**
     * Save or update an election.
     *
     * Update logic: If an election with the same ID already exists, update its fields and collections.
     * This is needed to allow re-importing or updating elections from new XML files without duplicate key errors.
     */
    @Transactional
    public Election saveElection(Election election) {
        logger.info("Saving election: {} with {} parties, {} candidates, {} elected candidates",
                election.getId(),
                election.getParties().size(),
                election.getCandidates().size(),
                election.getElectedCandidates().size());

        Optional<Election> existingElection = electionRepository.findById(election.getId());

        if (existingElection.isPresent()) {
            logger.warn("Election {} already exists in database, updating...", election.getId());
            Election existing = existingElection.get();
            existing.setName(election.getName());
            existing.setDate(election.getDate());

            existing.getParties().clear();
            existing.getParties().addAll(election.getParties());

            existing.getCandidates().clear();
            existing.getCandidates().addAll(election.getCandidates());

            existing.getElectedCandidates().clear();
            existing.getElectedCandidates().addAll(election.getElectedCandidates());

            existing.getConstituencies().clear();
            existing.getConstituencies().addAll(election.getConstituencies());

            existing.getPartyResults().clear();
            existing.getPartyResults().addAll(election.getPartyResults());

            existing.getMunicipalityResults().clear();
            existing.getMunicipalityResults().addAll(election.getMunicipalityResults());

            return electionRepository.save(existing);
        } else {
            return electionRepository.save(election);
        }
    }


    /**
     * Find election by ID
     */
    public Optional<Election> findById(String id) {
        return electionRepository.findById(id);
    }


    /**
     * Find all elections
     */
    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    /**
     * Delete an election
     */
    @Transactional
    public void delete(Election election) {
        electionRepository.delete(election);
    }

    /**
     * Delete election by ID
     */
    @Transactional
    public void deleteById(String id) {
        electionRepository.deleteById(id);
    }

    /**
     * Check if election exists by ID
     */
    public boolean existsByElectionId(String electionId) {
        return electionRepository.existsByElectionId(electionId);
    }

    /**
     * Find municipality by ID
     */
    public Municipality findMunicipalityById(String municipalityId) {
        return municipalityRepository.findById(municipalityId);
    }

    /**
     * Find municipality by name
     */
    public Municipality findMunicipalityByName(String municipalityName) {
        return municipalityRepository.findByName(municipalityName);
    }

    /**
     * Find municipalities for an election
     * Note: Municipalities are now accessed through constituencies
     * Use election.getConstituencies() and then constituency.getMunicipality()
     */
    public List<Municipality> findMunicipalitiesByElection(Election election) {
        return municipalityRepository.findByElection(election);
    }

    /**
     * Save municipality
     */
    @Transactional
    public Municipality saveMunicipality(Municipality municipality) {
        return municipalityRepository.save(municipality);
    }
}
