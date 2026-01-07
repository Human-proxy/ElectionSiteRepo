package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandidateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the count of seats won by each party in the specified election.
     * The result is a list of Object arrays, each containing the party ID, party name, and the number of seats won.
     *
     * @param electionId the ID of the election
     * @return a list of Object arrays representing the seat count for each party
     */
    public List<Object[]> findSeatCountsByElection(String electionId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT c.party.partyId, c.party.name, COUNT(c.pk) " +
                        "FROM Candidate c " +
                        "WHERE c.election.id = :electionId " +
                        "AND c.elected = true " +
                        "AND c.party IS NOT NULL " +
                        "GROUP BY c.party.partyId, c.party.name",
                Object[].class
        );
        query.setParameter("electionId", electionId);
        return query.getResultList();
    }
}
