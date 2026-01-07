package nl.hva.dederdekamer.election_backend.config;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElectionConfig {

    @Bean
    public Election election() {
        // Create and return your Election object here
        // You can later make this parse the XML file if you want
        return new Election("TK2023");
    }
}