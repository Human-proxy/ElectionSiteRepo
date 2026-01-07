package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml;

/**
 * Bundle class that groups all transformer instances together.
 * This eliminates the need for a constructor with 6 parameters and makes
 * the code more maintainable and easier to extend.
 */
public class TransformerBundle {
    private final DefinitionTransformer definitionTransformer;
    private final CandidateTransformer candidateTransformer;
    private final VotesTransformer resultTransformer;
    private final VotesTransformer nationalVotesTransformer;
    private final VotesTransformer constituencyVotesTransformer;
    private final VotesTransformer municipalityVotesTransformer;

    /**
     * Creates a new transformer bundle with all required transformers.
     *
     * @param definitionTransformer the transformer for processing structure files
     * @param candidateTransformer the transformer for processing candidate lists
     * @param resultTransformer the transformer for processing result files
     * @param nationalVotesTransformer the transformer for processing national votes
     * @param constituencyVotesTransformer the transformer for processing constituency votes
     * @param municipalityVotesTransformer the transformer for processing municipality votes
     */
    public TransformerBundle(DefinitionTransformer definitionTransformer,
                           CandidateTransformer candidateTransformer,
                           VotesTransformer resultTransformer,
                           VotesTransformer nationalVotesTransformer,
                           VotesTransformer constituencyVotesTransformer,
                           VotesTransformer municipalityVotesTransformer) {
        this.definitionTransformer = definitionTransformer;
        this.candidateTransformer = candidateTransformer;
        this.resultTransformer = resultTransformer;
        this.nationalVotesTransformer = nationalVotesTransformer;
        this.constituencyVotesTransformer = constituencyVotesTransformer;
        this.municipalityVotesTransformer = municipalityVotesTransformer;
    }

    // Getters
    public DefinitionTransformer getDefinitionTransformer() {
        return definitionTransformer;
    }

    public CandidateTransformer getCandidateTransformer() {
        return candidateTransformer;
    }

    public VotesTransformer getResultTransformer() {
        return resultTransformer;
    }

    public VotesTransformer getNationalVotesTransformer() {
        return nationalVotesTransformer;
    }

    public VotesTransformer getConstituencyVotesTransformer() {
        return constituencyVotesTransformer;
    }

    public VotesTransformer getMunicipalityVotesTransformer() {
        return municipalityVotesTransformer;
    }
}
