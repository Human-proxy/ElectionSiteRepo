package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.XMLParser.model.PartyResult;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Repository for PartyResult entity.
 * Handles queries for national-level party election results.
 */
@Repository
public class PartyResultRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find all party results for a specific election
     */
    public List<PartyResult> findByElectionId(String electionId) {
        TypedQuery<PartyResult> query = entityManager.createQuery(
                "SELECT pr FROM PartyResult pr WHERE pr.election.id = :electionId ORDER BY pr.totalVotes DESC",
                PartyResult.class);
        query.setParameter("electionId", electionId);
        return query.getResultList();
    }

    /**
     * Find result for a specific party in an election
     */
    public PartyResult findByElectionAndParty(String electionId, Long partyId) {
        TypedQuery<PartyResult> query = entityManager.createQuery(
                "SELECT pr FROM PartyResult pr " +
                        "WHERE pr.election.id = :electionId " +
                        "AND pr.party.partyId = :partyId",
                PartyResult.class);
        query.setParameter("electionId", electionId);
        query.setParameter("partyId", partyId);
        List<PartyResult> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Find all elected parties in an election
     */
    public List<PartyResult> findElectedByElection(String electionId) {
        TypedQuery<PartyResult> query = entityManager.createQuery(
                "SELECT pr FROM PartyResult pr " +
                        "WHERE pr.election.id = :electionId " +
                        "AND pr.elected = true " +
                        "ORDER BY pr.seats DESC, pr.totalVotes DESC",
                PartyResult.class);
        query.setParameter("electionId", electionId);
        return query.getResultList();
    }

    /**
     * Save a party result
     */
    public PartyResult save(PartyResult result) {
        if (result.getId() == null) {
            entityManager.persist(result);
            return result;
        } else {
            return entityManager.merge(result);
        }
    }

    /**
     * Count total results for an election
     */
    public long countByElection(String electionId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(pr) FROM PartyResult pr WHERE pr.election.id = :electionId",
                Long.class);
        query.setParameter("electionId", electionId);
        return query.getSingleResult();
    }
}
