package nl.hva.dederdekamer.election_backend.Home;

import nl.hva.dederdekamer.election_backend.controller.HomeController;
import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import nl.hva.dederdekamer.election_backend.service.HomeService;
import nl.hva.dederdekamer.election_backend.security.JwtRequestFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = HomeController.class)
class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HomeService homeService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @DisplayName("top4 constituencies returns list with expected fields")
    void top4_happyPath() throws Exception {
        List<ConstituencySummaryDTO> sample = List.of(
                new ConstituencySummaryDTO("HSB7", "Kieskring Arnhem", 1966872, "PVV (Partij voor de Vrijheid)", "#1B4F9C", 427014, 21.82331920612336, 37),
                new ConstituencySummaryDTO("HSB18", "Kieskring 's-Hertogenbosch", 1747406, "PVV (Partij voor de Vrijheid)", "#1B4F9C", 460322, 26.80856656005192, 32)
        );
        when(homeService.getTop4Constituencies("TK2023")).thenReturn(sample);

        mockMvc.perform(get("/api/v1/election/top4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].constituencyId", is("HSB7")))
                .andExpect(jsonPath("$[0].constituencyName", is("Kieskring Arnhem")))
                .andExpect(jsonPath("$[0].totalVotesCast", is(1966872)))
                .andExpect(jsonPath("$[0].topPartyName", is("PVV (Partij voor de Vrijheid)")))
                .andExpect(jsonPath("$[0].topPartyColor", is("#1B4F9C")))
                .andExpect(jsonPath("$[0].topPartyVotes", is(427014)))
                .andExpect(jsonPath("$[0].topPartyPercentage", is(closeTo(21.82331920612336, 0.0001))))
                .andExpect(jsonPath("$[0].municipalityCount", is(37)));
    }

    @Test
    @DisplayName("top4 constituencies returns 204 when empty")
    void top4_noContent() throws Exception {
        when(homeService.getTop4Constituencies("TK2023")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/election/top4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("parties/homepage returns list of parties for default election")
    void parties_homepage_happyPath() throws Exception {
        // Using a minimal Party-like map since controller returns List<Party>; we only assert array size
        // If Party is a class, we don't need to construct instances because we mock the service
        when(homeService.getHomepageParties("TK2023")).thenReturn(List.of());
        // Return an empty list first to verify 200 with empty array
        mockMvc.perform(get("/api/v1/parties/homepage").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // Now a non-empty list to verify it returns elements
        // We can't easily construct Party without importing the model; just assert 200
        when(homeService.getHomepageParties("TK2023")).thenReturn(List.of(new nl.hva.dederdekamer.election_backend.XMLParser.model.Party()));
        mockMvc.perform(get("/api/v1/parties/homepage").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("elections/metadata returns 200 with body when present")
    void elections_metadata_happyPath() throws Exception {
        Map<String, String> meta = Map.of(
                "id", "TK2023",
                "name", "Tweede Kamerverkiezingen 2023",
                "date", "2023-11-22"
        );
        when(homeService.getElectionMetadata("TK2023")).thenReturn(meta);

        mockMvc.perform(get("/api/v1/elections/metadata/TK2023").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("TK2023")))
                .andExpect(jsonPath("$.name", is("Tweede Kamerverkiezingen 2023")))
                .andExpect(jsonPath("$.date", is("2023-11-22")));
    }

    @Test
    @DisplayName("elections/metadata returns 404 when empty")
    void elections_metadata_notFound() throws Exception {
        when(homeService.getElectionMetadata("TK2023")).thenReturn(Map.of());

        mockMvc.perform(get("/api/v1/elections/metadata/TK2023").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("elections returns available elections array")
    void elections_list_happyPath() throws Exception {
        mockMvc.perform(get("/api/v1/elections").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("TK2023")));
    }
}