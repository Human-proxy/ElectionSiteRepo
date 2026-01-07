package nl.hva.dederdekamer.election_backend.XMLParser.factory;

import org.springframework.stereotype.Component;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.DutchElectionParser;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.TransformerBundle;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchCandidateTransformer;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchConstituencyVotesTransformer;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchDefinitionTransformer;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchMunicipalityVotesTransformer;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchNationalVotesTransformer;
import nl.hva.dederdekamer.election_backend.XMLParser.utils.xml.transformers.DutchResultTransformer;

/**
 * Factory component for creating DutchElectionParser instances with all required transformers.
 * This factory encapsulates the complex construction logic and makes the code more maintainable.
 * Uses the TransformerBundle pattern to reduce constructor complexity.
 */
@Component
public class DutchElectionParserFactory {

    /**
     * Creates a new DutchElectionParser instance configured with all necessary transformers
     * for the given election.
     * 
     * @param election The election instance to configure the transformers for
     * @return A fully configured DutchElectionParser instance
     */
    public DutchElectionParser createParser(Election election) {
        TransformerBundle transformers = createTransformerBundle(election);
        return new DutchElectionParser(transformers);
    }

    /**
     * Creates a TransformerBundle with all necessary transformers for the given election.
     * This method separates the concern of creating transformers from creating the parser.
     * 
     * @param election The election instance to configure the transformers for
     * @return A bundle containing all configured transformers
     */
    private TransformerBundle createTransformerBundle(Election election) {
        return new TransformerBundle(
                new DutchDefinitionTransformer(election),
                new DutchCandidateTransformer(election),
                new DutchResultTransformer(election),
                new DutchNationalVotesTransformer(election),
                new DutchConstituencyVotesTransformer(election),
                new DutchMunicipalityVotesTransformer(election)
        );
    }
}