package nl.hva.dederdekamer.election_backend.Home;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Election;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.XMLParser.service.DutchElectionService;
import nl.hva.dederdekamer.election_backend.dto.ConstituencySummaryDTO;
import nl.hva.dederdekamer.election_backend.service.ConstituencyService;
import nl.hva.dederdekamer.election_backend.service.HomeServiceImpl;
import nl.hva.dederdekamer.election_backend.service.PartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeServiceImplTest {

    @Mock
    private DutchElectionService dutchElectionService;

    @Mock
    private PartyService partyService;

    @Mock
    private ConstituencyService constituencyService;

    @InjectMocks
    private HomeServiceImpl homeService;

    private Election mockElection;
    private Party party1;
    private Party party2;

    @BeforeEach
    void setUp() {
        mockElection = new Election("TK2023");
        mockElection.setName("Tweede Kamerverkiezingen 2023");
        mockElection.setDate("2023-11-22");

        party1 = new Party();
        party1.setPartyId(1L);
        party1.setName("VVD");
        party1.setSeats(25);
        party1.setElected(true);

        party2 = new Party();
        party2.setPartyId(2L);
        party2.setName("PVV (Partij voor de Vrijheid)");
        party2.setSeats(37);
        party2.setElected(true);

        mockElection.addParty(party1);
        mockElection.addParty(party2);
    }

    @Test
    @DisplayName("getHomepageParties should return elected parties for given election")
    void testGetHomepageParties() {
        // Arrange
        List<Party> expectedParties = List.of(party1, party2);
        when(partyService.getElectedPartiesByElection("TK2023")).thenReturn(expectedParties);

        // Act
        List<Party> result = homeService.getHomepageParties("TK2023");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(party1, party2);
        verify(partyService, times(1)).getElectedPartiesByElection("TK2023");
    }

    @Test
    @DisplayName("getHomepageElectionResults should return election with only elected parties")
    void testGetHomepageElectionResults() {
        // Arrange
        when(dutchElectionService.getElectionById("TK2023")).thenReturn(Optional.of(mockElection));

        // Act
        Election result = homeService.getHomepageElectionResults("TK2023");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("TK2023");
        assertThat(result.getName()).isEqualTo("Tweede Kamerverkiezingen 2023");
        assertThat(result.getDate()).isEqualTo("2023-11-22");
        assertThat(result.getParties()).hasSize(2);
        verify(dutchElectionService, times(1)).getElectionById("TK2023");
    }

    @Test
    @DisplayName("getHomepageElectionResults should return null when election not found")
    void testGetHomepageElectionResults_NotFound() {
        // Arrange
        when(dutchElectionService.getElectionById("TK2025")).thenReturn(Optional.empty());

        // Act
        Election result = homeService.getHomepageElectionResults("TK2025");

        // Assert
        assertThat(result).isNull();
        verify(dutchElectionService, times(1)).getElectionById("TK2025");
    }

    @Test
    @DisplayName("getElectionMetadata should return metadata map with id, name, date")
    void testGetElectionMetadata() {
        // Arrange
        when(dutchElectionService.getElectionById("TK2023")).thenReturn(Optional.of(mockElection));

        // Act
        Map<String, String> result = homeService.getElectionMetadata("TK2023");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).containsEntry("id", "TK2023");
        assertThat(result).containsEntry("name", "Tweede Kamerverkiezingen 2023");
        assertThat(result).containsEntry("date", "2023-11-22");
    }

    @Test
    @DisplayName("getElectionMetadata should return empty map when election not found")
    void testGetElectionMetadata_NotFound() {
        // Arrange
        when(dutchElectionService.getElectionById("TK2025")).thenReturn(Optional.empty());

        // Act
        Map<String, String> result = homeService.getElectionMetadata("TK2025");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getTop4Constituencies should return list of constituency summaries")
    void testGetTop4Constituencies() {
        // Arrange
        List<ConstituencySummaryDTO> expectedConstituencies = List.of(
                new ConstituencySummaryDTO("HSB7", "Kieskring Arnhem", 1966872, 
                        "PVV (Partij voor de Vrijheid)", "#1B4F9C", 427014, 21.82, 37),
                new ConstituencySummaryDTO("HSB18", "Kieskring 's-Hertogenbosch", 1747406, 
                        "PVV (Partij voor de Vrijheid)", "#1B4F9C", 460322, 26.81, 32)
        );
        when(constituencyService.getTop4Constituencies("TK2023")).thenReturn(expectedConstituencies);

        // Act
        List<ConstituencySummaryDTO> result = homeService.getTop4Constituencies("TK2023");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getConstituencyId()).isEqualTo("HSB7");
        assertThat(result.get(0).getTotalVotesCast()).isEqualTo(1966872);
        assertThat(result.get(0).getTopPartyName()).isEqualTo("PVV (Partij voor de Vrijheid)");
        assertThat(result.get(0).getMunicipalityCount()).isEqualTo(37);
        verify(constituencyService, times(1)).getTop4Constituencies("TK2023");
    }

    @Test
    @DisplayName("getTop4Constituencies should return empty list when no data available")
    void testGetTop4Constituencies_Empty() {
        // Arrange
        when(constituencyService.getTop4Constituencies("TK2023")).thenReturn(List.of());

        // Act
        List<ConstituencySummaryDTO> result = homeService.getTop4Constituencies("TK2023");

        // Assert
        assertThat(result).isEmpty();
        verify(constituencyService, times(1)).getTop4Constituencies("TK2023");
    }
}
