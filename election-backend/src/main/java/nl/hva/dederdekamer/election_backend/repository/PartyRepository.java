package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Party entity.
 * Provides methods to retrieve party data from the database.
 */
public interface PartyRepository {

    /**
     * Finds all parties with seats > 0 (elected parties).
     *
     * @return list of elected parties
     */
    List<Party> findAllElectedParties();

    /**
     * Finds all parties.
     *
     * @return list of all parties
     */
    List<Party> findAll();

    /**
     * Finds a party by its party ID and election ID.
     *
     * @param partyId party ID
     * @param electionId election ID
     * @return optional party
     */
    Optional<Party> findByIdAndElection(String partyId, String electionId);

    /**
     * Finds all elected parties (seats > 0) for a specific election.
     *
     * @param electionId the election ID to filter by
     * @return list of elected parties for the given election
     */
    List<Party> findElectedPartiesByElection(String electionId);

    /**
     * Finds all parties for a specific election.
     *
     * @param electionId the election ID to filter by
     * @return list of all parties for the given election
     */
    List<Party> findPartiesByElection(String electionId);

    /**
     * Find a party by its database Primary Key (Long ID).
     */
    Optional<Party> findById(Long id);
}
