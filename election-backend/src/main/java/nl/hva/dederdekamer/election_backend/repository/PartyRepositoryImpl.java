package nl.hva.dederdekamer.election_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link PartyRepository} using {@link EntityManager}.
 */
@Repository
@Transactional(readOnly = true)
public class PartyRepositoryImpl implements PartyRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Party> findAllElectedParties() {
        TypedQuery<Party> q = em.createQuery(
            "SELECT p FROM Party p WHERE p.seats > 0 ORDER BY p.seats DESC", Party.class);
        return q.getResultList();
    }

    @Override
    public List<Party> findAll() {
        TypedQuery<Party> q = em.createQuery(
            "SELECT p FROM Party p", Party.class);
        return q.getResultList();
    }

    @Override
    public Optional<Party> findByIdAndElection(String partyId, String electionId) {
        TypedQuery<Party> q = em.createQuery(
            "SELECT p FROM Party p WHERE p.id = :partyId AND p.election.id = :electionId", 
            Party.class);
        q.setParameter("partyId", partyId);
        q.setParameter("electionId", electionId);
        List<Party> results = q.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Party> findElectedPartiesByElection(String electionId) {
        TypedQuery<Party> q = em.createQuery(
            "SELECT p FROM Party p WHERE p.seats > 0 AND p.election.id = :electionId ORDER BY p.seats DESC", 
            Party.class);
        q.setParameter("electionId", electionId);
        return q.getResultList();
    }

    @Override
    public List<Party> findPartiesByElection(String electionId) {
        TypedQuery<Party> q = em.createQuery(
            "SELECT p FROM Party p WHERE p.election.id = :electionId ORDER BY p.totalVotes DESC", 
            Party.class);
        q.setParameter("electionId", electionId);
        return q.getResultList();
    }

/**
 * Finds a party by its primary key.
 *
 * This method retrieves a {@code Party} entity using the JPA {@code find}
 * operation and wraps the result in an {@code Optional}. If no party exists
 * with the given ID, an empty {@code Optional} is returned.
 *
 * @param id the primary key of the party to look up
 * @return an {@code Optional} containing the found party, or empty if not found
 */
@Override
public Optional<Party> findById(Long id) {
    Party party = em.find(Party.class, id);
    return Optional.ofNullable(party);
}

}
