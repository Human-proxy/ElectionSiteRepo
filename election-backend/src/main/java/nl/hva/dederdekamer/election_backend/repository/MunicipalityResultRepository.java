package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.XMLParser.model.MunicipalityResult;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Repository for MunicipalityResult entity.
 * Handles queries for municipality-level election results.
 */
@Repository
public class MunicipalityResultRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find all municipality results for a specific election
     */
    public List<MunicipalityResult> findByElectionId(String electionId) {
        TypedQuery<MunicipalityResult> query = entityManager.createQuery(
                "SELECT mr FROM MunicipalityResult mr WHERE mr.election.id = :electionId",
                MunicipalityResult.class);
        query.setParameter("electionId", electionId);
        return query.getResultList();
    }

    /**
     * Find all party results for a specific municipality in an election
     */
    public List<MunicipalityResult> findByElectionAndMunicipality(String electionId, String municipalityId) {
        TypedQuery<MunicipalityResult> query = entityManager.createQuery(
                "SELECT mr FROM MunicipalityResult mr " +
                        "WHERE mr.election.id = :electionId " +
                        "AND mr.municipality.id = :municipalityId " +
                        "ORDER BY mr.totalVotes DESC",
                MunicipalityResult.class);
        query.setParameter("electionId", electionId);
        query.setParameter("municipalityId", municipalityId);
        return query.getResultList();
    }

    /**
     * Find all party results for a specific municipality by name in an election.
     * Uses JOIN FETCH to eagerly load the party to avoid N+1 query problem.
     */
    public List<MunicipalityResult> findByElectionAndMunicipalityName(String electionId, String municipalityName) {
        TypedQuery<MunicipalityResult> query = entityManager.createQuery(
                "SELECT mr FROM MunicipalityResult mr " +
                        "JOIN FETCH mr.party " +
                        "WHERE mr.election.id = :electionId " +
                        "AND mr.municipality.name = :municipalityName " +
                        "ORDER BY mr.totalVotes DESC",
                MunicipalityResult.class);
        query.setParameter("electionId", electionId);
        query.setParameter("municipalityName", municipalityName);
        return query.getResultList();
    }

    /**
     * Find results for a specific party across all municipalities in an election
     */
    public List<MunicipalityResult> findByElectionAndParty(String electionId, Long partyId) {
        TypedQuery<MunicipalityResult> query = entityManager.createQuery(
                "SELECT mr FROM MunicipalityResult mr " +
                        "WHERE mr.election.id = :electionId " +
                        "AND mr.party.partyId = :partyId " +
                        "ORDER BY mr.totalVotes DESC",
                MunicipalityResult.class);
        query.setParameter("electionId", electionId);
        query.setParameter("partyId", partyId);
        return query.getResultList();
    }

    /**
     * Save a municipality result
     */
    public MunicipalityResult save(MunicipalityResult result) {
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
                "SELECT COUNT(mr) FROM MunicipalityResult mr WHERE mr.election.id = :electionId",
                Long.class);
        query.setParameter("electionId", electionId);
        return query.getSingleResult();
    }

    /**
     * Get winning party (by votes) for each municipality in bulk.
     * Returns map of municipality name -> winning party info.
     * This is optimized for map coloring to avoid N+1 queries.
     * 
     * @param electionId the election ID
     * @return Map of municipality name to Object[]: [partyName, partyShortcode, partyColor]
     */
    public List<Object[]> findWinningPartyByMunicipality(String electionId) {
        String sql = """
            SELECT 
                m.name,
                p.name,
                p.shortcode,
                p.color
            FROM MunicipalityResult mr
            JOIN mr.municipality m
            JOIN mr.party p
            WHERE mr.election.id = :electionId
            AND mr.totalVotes = (
                SELECT MAX(mr2.totalVotes)
                FROM MunicipalityResult mr2
                WHERE mr2.municipality.id = m.id
                AND mr2.election.id = :electionId
            )
            ORDER BY m.name
            """;
        
        return entityManager.createQuery(sql, Object[].class)
                .setParameter("electionId", electionId)
                .getResultList();
    }

    /**
     * Get aggregated constituency vote totals with top party information.
     * Groups municipality results by constituency and calculates totals.
     * Orders by total votes descending for top-N queries.
     * 
     * @param electionId the election to query
     * @return list of Object arrays: [constituencyId, constituencyName, totalVotes, topPartyName, topPartyVotes, municipalityCount]
     */
    public List<Object[]> findConstituencySummariesByElection(String electionId) {
        // First get total votes per constituency
        String sql = """
            SELECT 
                c.id as constituencyId,
                c.name as constituencyName,
                SUM(mr.totalVotes) as totalVotes,
                COUNT(DISTINCT m.id) as municipalityCount
            FROM MunicipalityResult mr
            JOIN mr.municipality m
            JOIN m.constituency c
            WHERE mr.election.id = :electionId
            GROUP BY c.id, c.name
            ORDER BY totalVotes DESC
            """;
        
        return entityManager.createQuery(sql, Object[].class)
                .setParameter("electionId", electionId)
                .getResultList();
    }

    /**
     * Get the top party (by votes) for a specific constituency.
     * 
     * @param electionId the election ID
     * @param constituencyId the constituency ID
     * @return Object array: [partyName, partyColor, totalVotes, percentage]
     */
    public Object[] findTopPartyForConstituency(String electionId, String constituencyId) {
        String sql = """
            SELECT 
                p.name as partyName,
                p.color as partyColor,
                SUM(mr.totalVotes) as totalVotes,
                AVG(mr.percentage) as avgPercentage
            FROM MunicipalityResult mr
            JOIN mr.municipality m
            JOIN m.constituency c
            JOIN mr.party p
            WHERE mr.election.id = :electionId 
            AND c.id = :constituencyId
            GROUP BY p.id, p.name, p.color
            ORDER BY totalVotes DESC
            """;
        
        List<Object[]> results = entityManager.createQuery(sql, Object[].class)
                .setParameter("electionId", electionId)
                .setParameter("constituencyId", constituencyId)
                .setMaxResults(1)
                .getResultList();
        
        return results.isEmpty() ? null : results.get(0);
    }
    /**
     * Finds municipalities by looking at their constituency's election.
     * navigating: Municipality -> Constituency -> Election.
     */
    public List<Municipality> findByConstituencyElectionId(String electionId) {
        return entityManager
                .createQuery("SELECT m FROM Municipality m WHERE m.constituency.election.id = :electionId", Municipality.class)
                .setParameter("electionId", electionId)
                .getResultList();
    }

    /**
     * Finds all municipalities that have results for a specific election.
     * Used for the Quiz dropdown to ensure we only show valid regions.
     */
    public List<Municipality> findDistinctMunicipalitiesByElectionId(String electionId) {
        TypedQuery<Municipality> query = entityManager.createQuery(
            "SELECT DISTINCT mr.municipality FROM MunicipalityResult mr " +
            "WHERE mr.election.id = :electionId " +
            "ORDER BY mr.municipality.name ASC", 
            Municipality.class);
        
        query.setParameter("electionId", electionId);
        return query.getResultList();
    }
}
