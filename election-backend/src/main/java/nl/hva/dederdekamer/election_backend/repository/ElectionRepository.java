package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ElectionRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Save or update an election
     */
    @Transactional
    public Election save(Election election) {
        if (election.getId() == null) {
            entityManager.persist(election);
            return election;
        } else {
            return entityManager.merge(election);
        }
    }
    
    /**
     * Find election by ID (String)
     */
    public Optional<Election> findById(String id) {
        Election election = entityManager.find(Election.class, id);
        return Optional.ofNullable(election);
    }
    
    
    /**
     * Find all elections
     */
    public List<Election> findAll() {
        return entityManager.createQuery("SELECT e FROM Election e", Election.class)
                .getResultList();
    }
    
    /**
     * Delete an election
     */
    @Transactional
    public void delete(Election election) {
        if (entityManager.contains(election)) {
            entityManager.remove(election);
        } else {
            entityManager.remove(entityManager.merge(election));
        }
    }
    
    /**
     * Delete election by ID (String)
     */
    @Transactional
    public void deleteById(String id) {
        Election election = entityManager.find(Election.class, id);
        if (election != null) {
            entityManager.remove(election);
        }
    }
    
    /**
     * Check if election exists by election ID
     */
    public boolean existsByElectionId(String electionId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM Election e WHERE e.id = :electionId", Long.class);
        query.setParameter("electionId", electionId);
        return query.getSingleResult() > 0;
    }
}