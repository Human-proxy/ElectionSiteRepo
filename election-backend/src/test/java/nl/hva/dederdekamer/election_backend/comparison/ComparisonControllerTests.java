package nl.hva.dederdekamer.election_backend.comparison;

import nl.hva.dederdekamer.election_backend.controller.ComparisonController;
import nl.hva.dederdekamer.election_backend.repository.CandidateRepository;
import nl.hva.dederdekamer.election_backend.security.JwtRequestFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Collections; // added

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link ComparisonController} focusing on the /api/elections/compare endpoint.
 */
@AutoConfigureMockMvc(addFilters = false) // disable security filters
@WebMvcTest(ComparisonController.class)
class ComparisonControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateRepository candidateRepository;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @DisplayName("compare elections happy path with VVD and merged GroenLinks-PvdA")
    void compareElections_happyPath() throws Exception {
        // Mock seat counts for TK2021 (year1)
        when(candidateRepository.findSeatCountsByElection("TK2021")).thenReturn(List.of(
                new Object[]{"1", "VVD", 34L},
                new Object[]{"5", "GROENLINKS", 8L},
                new Object[]{"7", "Partij van de Arbeid (P.v.d.A.)", 9L}
        ));
        // Mock seat counts for TK2023 (year2)
        when(candidateRepository.findSeatCountsByElection("TK2023")).thenReturn(List.of(
                new Object[]{"1", "VVD", 24L},
                new Object[]{"3", "GROENLINKS / Partij van de Arbeid (PvdA)", 25L}
        ));

        mockMvc.perform(get("/api/elections/compare")
                        .param("year1", "2021")
                        .param("year2", "2023")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year1").value(2021))
                .andExpect(jsonPath("$.year2").value(2023))
                // Expect two aggregated parties (VVD and GroenLinks-PvdA)
                .andExpect(jsonPath("$.parties", hasSize(2)))
                // Sorted alphabetically by partyName -> GroenLinks-PvdA first, VVD second
                .andExpect(jsonPath("$.parties[0].partyName").value("GroenLinks-PvdA"))
                .andExpect(jsonPath("$.parties[0].partyId").value("gl-pvda"))
                .andExpect(jsonPath("$.parties[0].seatsYear1").value(8 + 9))
                .andExpect(jsonPath("$.parties[0].seatsYear2").value(25))
                .andExpect(jsonPath("$.parties[0].difference").value(25 - (8 + 9)))
                .andExpect(jsonPath("$.parties[1].partyName").value("VVD"))
                .andExpect(jsonPath("$.parties[1].partyId").value("vvd"))
                .andExpect(jsonPath("$.parties[1].seatsYear1").value(34))
                .andExpect(jsonPath("$.parties[1].seatsYear2").value(24))
                .andExpect(jsonPath("$.parties[1].difference").value(24 - 34));
    }

    @Test
    @DisplayName("compare elections returns 404 when no data for both years")
    void compareElections_noData() throws Exception {
        when(candidateRepository.findSeatCountsByElection("TK2021")).thenReturn(Collections.emptyList());
        when(candidateRepository.findSeatCountsByElection("TK2023")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/elections/compare")
                        .param("year1", "2021")
                        .param("year2", "2023")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("compare elections unknown year returns 400")
    void compareElections_unknownYear() throws Exception {
        when(candidateRepository.findSeatCountsByElection("TK2023")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/elections/compare")
                        .param("year1", "1999")
                        .param("year2", "2023")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("compare elections default parameters (2017 vs 2023) happy path")
    void compareElections_defaultParams() throws Exception {
        when(candidateRepository.findSeatCountsByElection("TK2017")).thenReturn(Collections.singletonList(
                new Object[]{"1", "VVD", 33L}
        ));
        when(candidateRepository.findSeatCountsByElection("TK2023")).thenReturn(Collections.singletonList(
                new Object[]{"1", "VVD", 24L}
        ));

        mockMvc.perform(get("/api/elections/compare").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year1").value(2017))
                .andExpect(jsonPath("$.year2").value(2023))
                .andExpect(jsonPath("$.parties", hasSize(1)))
                .andExpect(jsonPath("$.parties[0].partyName").value("VVD"))
                .andExpect(jsonPath("$.parties[0].seatsYear1").value(33))
                .andExpect(jsonPath("$.parties[0].seatsYear2").value(24))
                .andExpect(jsonPath("$.parties[0].difference").value(24 - 33));
    }
}