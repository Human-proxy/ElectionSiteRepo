package nl.hva.dederdekamer.election_backend.municipality;

import nl.hva.dederdekamer.election_backend.XMLParser.model.Municipality;
import nl.hva.dederdekamer.election_backend.XMLParser.model.MunicipalityResult;
import nl.hva.dederdekamer.election_backend.XMLParser.model.Party;
import nl.hva.dederdekamer.election_backend.dto.MunicipalityResultsDTO;
import nl.hva.dederdekamer.election_backend.exception.InvalidRequestException;
import nl.hva.dederdekamer.election_backend.exception.ResourceNotFoundException;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityRepository;
import nl.hva.dederdekamer.election_backend.repository.MunicipalityResultRepository;
import nl.hva.dederdekamer.election_backend.service.MunicipalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MunicipalityServiceTest {

    @Mock
    private MunicipalityRepository municipalityRepository;

    @Mock
    private MunicipalityResultRepository municipalityResultRepository;

    @InjectMocks
    private MunicipalityService municipalityService;

    private Municipality mockMunicipality;
    private Party mockParty1;
    private Party mockParty2;

    @BeforeEach
    void setUp() {
        mockMunicipality = new Municipality();
        mockMunicipality.setId("GM0363");
        mockMunicipality.setName("Amsterdam");

        mockParty1 = new Party();
        mockParty1.setPartyId(1L);
        mockParty1.setName("VVD");
        mockParty1.setShortcode("VVD");
        mockParty1.setColor("#FF6200");

        mockParty2 = new Party();
        mockParty2.setPartyId(2L);
        mockParty2.setName("PVV (Partij voor de Vrijheid)");
        mockParty2.setShortcode("PVV");
        mockParty2.setColor("#1B4F9C");
    }

    @Test
    @DisplayName("getMunicipalityDetails should return municipality when found")
    void testGetMunicipalityDetails_Success() {
        // Arrange
        when(municipalityRepository.findByName("Amsterdam")).thenReturn(mockMunicipality);

        // Act
        Municipality result = municipalityService.getMunicipalityDetails("Amsterdam");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("GM0363");
        assertThat(result.getName()).isEqualTo("Amsterdam");
        verify(municipalityRepository, times(1)).findByName("Amsterdam");
    }

    @Test
    @DisplayName("getMunicipalityDetails should throw exception when municipality not found")
    void testGetMunicipalityDetails_NotFound() {
        // Arrange
        when(municipalityRepository.findByName("NonExistent")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> municipalityService.getMunicipalityDetails("NonExistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Municipality")
                .hasMessageContaining("NonExistent");
    }

    @Test
    @DisplayName("getMunicipalityDetails should throw exception for null or empty name")
    void testGetMunicipalityDetails_InvalidInput() {
        // Act & Assert
        assertThatThrownBy(() -> municipalityService.getMunicipalityDetails(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> municipalityService.getMunicipalityDetails(""))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> municipalityService.getMunicipalityDetails("   "))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("getMunicipalityResults should return results DTO with party data")
    void testGetMunicipalityResults_Success() {
        // Arrange
        MunicipalityResult result1 = new MunicipalityResult();
        result1.setMunicipality(mockMunicipality);
        result1.setParty(mockParty1);
        result1.setTotalVotes(15000);
        result1.setPercentage(20.5);

        MunicipalityResult result2 = new MunicipalityResult();
        result2.setMunicipality(mockMunicipality);
        result2.setParty(mockParty2);
        result2.setTotalVotes(18000);
        result2.setPercentage(24.6);

        List<MunicipalityResult> mockResults = List.of(result1, result2);
        when(municipalityResultRepository.findByElectionAndMunicipalityName("TK2023", "Amsterdam"))
                .thenReturn(mockResults);

        // Act
        MunicipalityResultsDTO result = municipalityService.getMunicipalityResults("TK2023", "Amsterdam");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMunicipalityName()).isEqualTo("Amsterdam");
        assertThat(result.getMunicipalityId()).isEqualTo("GM0363");
        assertThat(result.getPartyResults()).hasSize(2);
        
        assertThat(result.getPartyResults().get(0).getPartyName()).isEqualTo("VVD");
        assertThat(result.getPartyResults().get(0).getTotalVotes()).isEqualTo(15000);
        assertThat(result.getPartyResults().get(0).getPercentage()).isEqualTo(20.5);
        assertThat(result.getPartyResults().get(0).getColor()).isEqualTo("#FF6200");
        
        assertThat(result.getPartyResults().get(1).getPartyName()).isEqualTo("PVV (Partij voor de Vrijheid)");
        assertThat(result.getPartyResults().get(1).getTotalVotes()).isEqualTo(18000);
        assertThat(result.getPartyResults().get(1).getPercentage()).isEqualTo(24.6);
    }

    @Test
    @DisplayName("getMunicipalityResults should throw exception when no results found")
    void testGetMunicipalityResults_NoResults() {
        // Arrange
        when(municipalityResultRepository.findByElectionAndMunicipalityName("TK2023", "Amsterdam"))
                .thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> municipalityService.getMunicipalityResults("TK2023", "Amsterdam"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Amsterdam")
                .hasMessageContaining("TK2023");
    }

    @Test
    @DisplayName("getMunicipalityResults should throw exception for invalid input")
    void testGetMunicipalityResults_InvalidInput() {
        // Act & Assert - null election ID
        assertThatThrownBy(() -> municipalityService.getMunicipalityResults(null, "Amsterdam"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("election ID")
                .hasMessageContaining("cannot be null or empty");

        // Empty election ID
        assertThatThrownBy(() -> municipalityService.getMunicipalityResults("", "Amsterdam"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("election ID");

        // Null municipality name
        assertThatThrownBy(() -> municipalityService.getMunicipalityResults("TK2023", null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("municipality name")
                .hasMessageContaining("cannot be null or empty");

        // Empty municipality name
        assertThatThrownBy(() -> municipalityService.getMunicipalityResults("TK2023", ""))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("municipality name");
    }

    @Test
    @DisplayName("getMunicipalityData should return municipality from repository")
    void testGetMunicipalityData() {
        // Arrange
        when(municipalityRepository.findByName("Amsterdam")).thenReturn(mockMunicipality);

        // Act
        Municipality result = municipalityService.getMunicipalityData("Amsterdam");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("GM0363");
        assertThat(result.getName()).isEqualTo("Amsterdam");
        verify(municipalityRepository, times(1)).findByName("Amsterdam");
    }

    @Test
    @DisplayName("getMunicipalityResults should correctly map all party result fields")
    void testGetMunicipalityResults_FieldMapping() {
        // Arrange
        MunicipalityResult result1 = new MunicipalityResult();
        result1.setMunicipality(mockMunicipality);
        result1.setParty(mockParty1);
        result1.setTotalVotes(25000);
        result1.setPercentage(33.5);

        when(municipalityResultRepository.findByElectionAndMunicipalityName("TK2023", "Amsterdam"))
                .thenReturn(List.of(result1));

        // Act
        MunicipalityResultsDTO result = municipalityService.getMunicipalityResults("TK2023", "Amsterdam");

        // Assert
        MunicipalityResultsDTO.PartyResultDTO partyResult = result.getPartyResults().get(0);
        assertThat(partyResult.getPartyId()).isEqualTo(1L);
        assertThat(partyResult.getPartyName()).isEqualTo("VVD");
        assertThat(partyResult.getPartyShortcode()).isEqualTo("VVD");
        assertThat(partyResult.getTotalVotes()).isEqualTo(25000);
        assertThat(partyResult.getPercentage()).isEqualTo(33.5);
        assertThat(partyResult.getColor()).isEqualTo("#FF6200");
    }
}
