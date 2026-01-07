package nl.hva.dederdekamer.election_backend.municipality;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.controller.MunicipalityController;
import nl.hva.dederdekamer.election_backend.dto.MunicipalityResultsDTO;
import nl.hva.dederdekamer.election_backend.security.JwtRequestFilter;
import nl.hva.dederdekamer.election_backend.service.MunicipalityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = MunicipalityController.class)
class MunicipalityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MunicipalityService municipalityService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @DisplayName("getMunicipalityDetailsByName returns municipality data")
    void testGetMunicipalityDetailsByName() throws Exception {
        // Arrange
        Municipality municipality = new Municipality();
        municipality.setId("GM0363");
        municipality.setName("Amsterdam");
        
        when(municipalityService.getMunicipalityDetails("Amsterdam")).thenReturn(municipality);

        // Act & Assert
        mockMvc.perform(get("/api/elections/TK2023/municipalities/Amsterdam")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("GM0363")))
                .andExpect(jsonPath("$.name", is("Amsterdam")));
    }

    @Test
    @DisplayName("getMunicipalityResults returns party results for municipality")
    void testGetMunicipalityResults() throws Exception {
        // Arrange
        List<MunicipalityResultsDTO.PartyResultDTO> partyResults = List.of(
                new MunicipalityResultsDTO.PartyResultDTO(1L, "VVD", "VVD", 15000, 20.5, "#FF6200"),
                new MunicipalityResultsDTO.PartyResultDTO(2L, "PVV (Partij voor de Vrijheid)", "PVV", 18000, 24.6, "#1B4F9C")
        );
        
        MunicipalityResultsDTO resultsDTO = new MunicipalityResultsDTO("Amsterdam", "GM0363", partyResults);
        
        when(municipalityService.getMunicipalityResults("TK2023", "Amsterdam")).thenReturn(resultsDTO);

        // Act & Assert
        mockMvc.perform(get("/api/elections/TK2023/municipalities/Amsterdam/results")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.municipalityName", is("Amsterdam")))
                .andExpect(jsonPath("$.municipalityId", is("GM0363")))
                .andExpect(jsonPath("$.partyResults", hasSize(2)))
                .andExpect(jsonPath("$.partyResults[0].partyName", is("VVD")))
                .andExpect(jsonPath("$.partyResults[0].totalVotes", is(15000)))
                .andExpect(jsonPath("$.partyResults[0].percentage", is(20.5)))
                .andExpect(jsonPath("$.partyResults[0].color", is("#FF6200")))
                .andExpect(jsonPath("$.partyResults[1].partyName", is("PVV (Partij voor de Vrijheid)")))
                .andExpect(jsonPath("$.partyResults[1].totalVotes", is(18000)))
                .andExpect(jsonPath("$.partyResults[1].percentage", is(24.6)));
    }

    @Test
    @DisplayName("getAllMunicipalities returns list of municipalities")
    void testGetAllMunicipalities() throws Exception {
        // Arrange
        List<Object> municipalities = List.of(
                Map.of("id", "GM0363", "name", "Amsterdam"),
                Map.of("id", "GM0518", "name", "Rotterdam"),
                Map.of("id", "GM0344", "name", "Utrecht")
        );
        
        when(municipalityService.getAllMunicipalitiesBasic("TK2023")).thenReturn(municipalities);

        // Act & Assert
        mockMvc.perform(get("/api/elections/TK2023/municipalities")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is("GM0363")))
                .andExpect(jsonPath("$[0].name", is("Amsterdam")))
                .andExpect(jsonPath("$[1].name", is("Rotterdam")))
                .andExpect(jsonPath("$[2].name", is("Utrecht")));
    }

    @Test
    @DisplayName("getMunicipalityResults returns results with correct structure")
    void testGetMunicipalityResultsStructure() throws Exception {
        // Arrange
        List<MunicipalityResultsDTO.PartyResultDTO> partyResults = List.of(
                new MunicipalityResultsDTO.PartyResultDTO(1L, "D66", "D66", 12000, 18.2, "#00A854"),
                new MunicipalityResultsDTO.PartyResultDTO(2L, "GroenLinks", "GL", 10000, 15.1, "#86BE4D")
        );
        
        MunicipalityResultsDTO resultsDTO = new MunicipalityResultsDTO("Utrecht", "GM0344", partyResults);
        
        when(municipalityService.getMunicipalityResults("TK2023", "Utrecht")).thenReturn(resultsDTO);

        // Act & Assert
        mockMvc.perform(get("/api/elections/TK2023/municipalities/Utrecht/results")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.municipalityName").exists())
                .andExpect(jsonPath("$.municipalityId").exists())
                .andExpect(jsonPath("$.partyResults").isArray())
                .andExpect(jsonPath("$.partyResults[0].partyId").exists())
                .andExpect(jsonPath("$.partyResults[0].partyName").exists())
                .andExpect(jsonPath("$.partyResults[0].partyShortcode").exists())
                .andExpect(jsonPath("$.partyResults[0].totalVotes").exists())
                .andExpect(jsonPath("$.partyResults[0].percentage").exists())
                .andExpect(jsonPath("$.partyResults[0].color").exists());
    }
}
