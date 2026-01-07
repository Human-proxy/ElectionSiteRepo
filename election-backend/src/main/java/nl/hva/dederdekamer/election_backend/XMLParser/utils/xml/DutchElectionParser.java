package nl.hva.dederdekamer.election_backend.XMLParser.utils.xml;

import nl.hva.dederdekamer.election_backend.XMLParser.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Processes the XML data files for the Dutch elections. It is completely model agnostic. This means that it
 * doesn't have any knowledge of the data model that is being used by the application. All the datamodel specific
 * logic must be provided by a separate classes that implements one of the Transformer interfaces.<br>
 * It can process all the files that are provided by the <a href="https://data.overheid.nl/datasets?sort=score%20desc%2C%20sys_modified%20desc&facet_authority%5B0%5D=http%3A//standaarden.overheid.nl/owms/terms/Kiesraad">Kiesraad</a>.
 * It behaves similar as the
 * <a href="https://www.baeldung.com/java-visitor-pattern">visitor pattern</a>.<br>
 * <br>
 * The data in the XML files has a more or less hierarchy structure. When a method of the transformer is called, a
 * {@link Map} containing all the information on that level, including the information at the higher levels,
 * is provided. The {@link Map} is specified as: Map&lt;String, String>. It is up to the transformer to convert any
 * numerical information from its {@link String} representation into its appropriate datatype.<br>
 * <br>
 * <em>It assumes that filenames have NOT been changed and that the content has not been altered!</em><br>
 * <em>Most likely you don't have to alter this class, but if you feel you need to, please feel free :-)</em><br/>
 * <br/>
 * <i><b>Refactored: </b>Constructor simplified using TransformerBundle pattern and proper logging implemented.</i>
 */
public class DutchElectionParser {
    private static final Logger logger = LoggerFactory.getLogger(DutchElectionParser.class);
    
    private final DefinitionTransformer definitionTransformer;
    private final CandidateTransformer candidateTransformer;
    private final VotesTransformer resultTransformer;
    private final VotesTransformer nationalVotesTransformer;
    private final VotesTransformer constituencyVotesTransformer;
    private final VotesTransformer municipalityVotesTransformer;

    /**
     * Creates a new instance that will use the provided transformer bundle for transforming the data into the
     * application specific data model.
     * @param transformers Bundle containing all transformers needed for processing election data
     */
    public DutchElectionParser(TransformerBundle transformers) {
        this.definitionTransformer = transformers.getDefinitionTransformer();
        this.candidateTransformer = transformers.getCandidateTransformer();
        this.resultTransformer = transformers.getResultTransformer();
        this.nationalVotesTransformer = transformers.getNationalVotesTransformer();
        this.constituencyVotesTransformer = transformers.getConstituencyVotesTransformer();
        this.municipalityVotesTransformer = transformers.getMunicipalityVotesTransformer();
    }

    /**
     * Traverses all the folders within the specified folder and calls the appropriate methods of the transformer.
     * While processing the files it will skip any file that has a different election-id than the one specified.
     * Currently, it only processes the files containing the 'kieslijsten' and the votes per reporting unit.
     *
     * @param electionId the identifier for the of the files that should be processed, for example <i>TK2023</i>.
     * @param folderName The name of the folder that contains the files containing the election data.
     * @throws IOException in case something goes wrong while reading the file.
     * @throws XMLStreamException when a file has not the expected format.
     */
    public void parseResults(String electionId, String folderName) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Loading election data for '{}' from folder: {}", electionId, folderName);
        parseFiles(folderName, "Verkiezingsdefinitie_%s".formatted(electionId), new EMLHandler(definitionTransformer));

        parseFiles(folderName, "Kandidatenlijsten_%s".formatted(electionId), new EMLHandler(candidateTransformer));

        parseFiles(folderName, "Resultaat_%s".formatted(electionId), new EMLHandler(resultTransformer));

        parseFiles(folderName, "Totaaltelling_%s".formatted(electionId), new EMLHandler(nationalVotesTransformer));

        parseFiles(folderName, "Telling_%s_kieskring".formatted(electionId), new EMLHandler(constituencyVotesTransformer));

        parseFiles(folderName, "Telling_%s_gemeente".formatted(electionId), new EMLHandler(municipalityVotesTransformer));
    }

    private void parseFiles(String folderName, String fileFilter, EMLHandler emlHandler) throws IOException, ParserConfigurationException, SAXException {
        List<Path> files = PathUtils.findFilesToScan(folderName, fileFilter);
        files.sort(Comparator.comparing(Path::getFileName));

        for (Path electionFile : files) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(electionFile.toString()), 64 * 1024)) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser parser = factory.newSAXParser();
                emlHandler.setFileName(electionFile.toString());
                parser.parse(bis, emlHandler);
            }
        }
    }

    /**
     * Parse a single file. The parser instance is already bound to an Election via its transformers
     * (created by the factory). We select an appropriate EMLHandler depending on the file name.
     */
    public void parseSingleFile(Path electionFile) throws IOException, ParserConfigurationException, SAXException {
        String fileName = electionFile.getFileName().toString();
        EMLHandler handler = null;

        // Determine which handler (transformer) should process this file
        if (fileName.startsWith("Verkiezingsdefinitie_")) {
            handler = new EMLHandler(definitionTransformer);
        } else if (fileName.startsWith("Kandidatenlijsten_")) {
            handler = new EMLHandler(candidateTransformer);
        } else if (fileName.startsWith("Resultaat_")) {
            handler = new EMLHandler(resultTransformer);
        } else if (fileName.startsWith("Totaaltelling_")) {
            handler = new EMLHandler(nationalVotesTransformer);
        } else if (fileName.contains("kieskring") || fileName.startsWith("Telling_") && fileName.contains("kieskring")) {
            handler = new EMLHandler(constituencyVotesTransformer);
        } else if (fileName.contains("gemeente") || fileName.startsWith("Telling_") && fileName.contains("gemeente")) {
            handler = new EMLHandler(municipalityVotesTransformer);
        } else {
            logger.warn("Unrecognized election file: {} - skipping", fileName);
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(electionFile.toString()), 64 * 1024)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            handler.setFileName(electionFile.toString());
            parser.parse(bis, handler);
        }
    }
}
