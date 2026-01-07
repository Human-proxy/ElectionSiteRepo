package nl.hva.dederdekamer.election_backend.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import java.util.List;

/**
 * JPA implementation of {@link PartyRepository} using {@link EntityManager}.
 */
@Repository
@Transactional(readOnly = true)
public class MunicipalityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Municipality findByName(String municipalityName) {
        try {
            return entityManager.createQuery("SELECT m FROM Municipality m WHERE m.name = :name", Municipality.class)
                    .setParameter("name", municipalityName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Municipality findById(String municipalityId) {
        return entityManager.find(Municipality.class, municipalityId);
    }

    @Transactional
    public Municipality save(Municipality municipality) {
        if (municipality.getId() == null || entityManager.find(Municipality.class, municipality.getId()) == null) {
            entityManager.persist(municipality);
        } else {
            municipality = entityManager.merge(municipality);
        }
        return municipality;
    }

    public List<Municipality> findByElection(Election election) {
        return entityManager
                .createQuery("SELECT m FROM Municipality m WHERE m.election = :election", Municipality.class)
                .setParameter("election", election)
                .getResultList();
    }

    public List<Municipality> findByElectionId(String electionId) {
        return entityManager
                .createQuery("SELECT m FROM Municipality m WHERE m.election.id = :electionId", Municipality.class)
                .setParameter("electionId", electionId)
                .getResultList();
    }

    public Municipality findByMunicipalityId(String municipalityId) {
        try {
            return entityManager
                    .createQuery(
                            "SELECT m FROM Municipality m WHERE m.election.id = :electionId AND m.id = :municipalityId",
                            Municipality.class)
                    .setParameter("electionId", "TK2023")
                    .setParameter("municipalityId", municipalityId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Municipality> findAll() {
        return entityManager.createQuery("SELECT m FROM Municipality m", Municipality.class)
                .getResultList();
    }
    
/**
 * Finds all municipalities that belong to the constituencies of a given election.
 *
 * This method performs a JPA query that retrieves every {@code Municipality}
 * where the linked constituency belongs to the election with the given ID.
 *
 * @param electionId the ID of the election whose municipalities should be returned
 * @return a list of municipalities associated with the specified election
 */
public List<Municipality> findByConstituencyElectionId(String electionId) {
    return entityManager
            .createQuery(
                    "SELECT m FROM Municipality m WHERE m.constituency.election.id = :electionId",
                    Municipality.class
            )
            .setParameter("electionId", electionId)
            .getResultList();
}

}
