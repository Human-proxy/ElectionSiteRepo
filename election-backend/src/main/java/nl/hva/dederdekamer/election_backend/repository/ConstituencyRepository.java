package nl.hva.dederdekamer.election_backend.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Constituency;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import java.util.List;

/**
 * JPA implementation of ConstituencyRepository using EntityManager.
 */
@Repository
@Transactional(readOnly = true)
public class ConstituencyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find constituency by ID
     */
    public Constituency findById(Long id) {
        return entityManager.find(Constituency.class, id);
    }

    /**
     * Find constituency by constituency ID (the external ID, not the database PK)
     */
    public Constituency findByConstituencyId(String constituencyId) {
        try {
            return entityManager
                    .createQuery("SELECT c FROM Constituency c WHERE c.id = :id", Constituency.class)
                    .setParameter("id", constituencyId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find constituency by name
     */
    public Constituency findByName(String name) {
        try {
            return entityManager
                    .createQuery("SELECT c FROM Constituency c WHERE c.name = :name", Constituency.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find all constituencies for an election
     */
    public List<Constituency> findByElection(Election election) {
        return entityManager
                .createQuery("SELECT c FROM Constituency c WHERE c.election = :election", Constituency.class)
                .setParameter("election", election)
                .getResultList();
    }

    /**
     * Find all constituencies for an election by election ID
     */
    public List<Constituency> findByElectionId(String electionId) {
        return entityManager
                .createQuery("SELECT c FROM Constituency c WHERE c.election.id = :electionId", Constituency.class)
                .setParameter("electionId", electionId)
                .getResultList();
    }

    /**
     * Find all constituencies
     */
    public List<Constituency> findAll() {
        return entityManager
                .createQuery("SELECT c FROM Constituency c", Constituency.class)
                .getResultList();
    }

    /**
     * Save or update a constituency
     */
    @Transactional
    public Constituency save(Constituency constituency) {
        if (constituency.getId() == null || entityManager.find(Constituency.class, constituency.getId()) == null) {
            entityManager.persist(constituency);
        } else {
            constituency = entityManager.merge(constituency);
        }
        return constituency;
    }

    /**
     * Delete a constituency
     */
    @Transactional
    public void delete(Constituency constituency) {
        if (entityManager.contains(constituency)) {
            entityManager.remove(constituency);
        } else {
            entityManager.remove(entityManager.merge(constituency));
        }
    }

    /**
     * Delete constituency by ID
     */
    @Transactional
    public void deleteById(Long id) {
        Constituency constituency = findById(id);
        if (constituency != null) {
            delete(constituency);
        }
    }
}