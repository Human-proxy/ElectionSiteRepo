package nl.hva.dederdekamer.election_backend.profile;

import nl.hva.dederdekamer.election_backend.controller.ProfileController;
import nl.hva.dederdekamer.election_backend.dto.ProfileUpdateResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.RoleEntity;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.model.RoleName;
import nl.hva.dederdekamer.election_backend.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProfileController using Mockito.
 * Tests both happy and unhappy flows for profile endpoints.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileController Tests")
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private UserEntity testUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        RoleEntity userRole = new RoleEntity(RoleName.USER);
        testUser = new UserEntity(
            "testuser",
            "test@example.com",
            "hashedPassword",
            true,
            new HashSet<>(Set.of(userRole))
        );

        userResponse = new UserResponse(
            "1",
            "testuser",
            "test@example.com",
            null,
            LocalDateTime.now(),
            new HashSet<>(),
            Set.of("USER"),
            null
        );
    }

    // ============= getProfile Tests =============

    @Test
    @DisplayName("getProfile - Happy Flow: Should return user profile with 200 OK")
    void getProfile_shouldReturnProfile() {
        // Arrange
        when(profileService.getProfile("testuser")).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = profileController.getProfile(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("testuser");
        verify(profileService, times(1)).getProfile("testuser");
    }

    // ============= updateProfile Tests =============

    @Test
    @DisplayName("updateProfile - Happy Flow: Should update profile and return 200 OK")
    void updateProfile_shouldUpdateProfileSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "newusername");
        request.put("email", "newemail@example.com");

        ProfileUpdateResponse updateResponse = new ProfileUpdateResponse(userResponse, "new-token");
        when(profileService.updateProfile("testuser", "newusername", "newemail@example.com"))
            .thenReturn(updateResponse);

        // Act
        ResponseEntity<?> response = profileController.updateProfile(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ProfileUpdateResponse.class);
        verify(profileService, times(1)).updateProfile("testuser", "newusername", "newemail@example.com");
    }

    @Test
    @DisplayName("updateProfile - Unhappy Flow: Should return 400 BAD_REQUEST for invalid data")
    void updateProfile_shouldReturn400ForInvalidData() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "newusername");
        request.put("email", "invalid-email");

        when(profileService.updateProfile("testuser", "newusername", "invalid-email"))
            .thenThrow(new IllegalArgumentException("Invalid email format"));

        // Act
        ResponseEntity<?> response = profileController.updateProfile(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid email format");
    }

    @Test
    @DisplayName("updateProfile - Unhappy Flow: Should return 500 for unexpected errors")
    void updateProfile_shouldReturn500ForUnexpectedError() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "newusername");

        when(profileService.updateProfile(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = profileController.updateProfile(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal server error");
    }

    // ============= updateProfileImage Tests =============

    @Test
    @DisplayName("updateProfileImage - Happy Flow: Should update image and return 200 OK")
    void updateProfileImage_shouldUpdateImageSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("profileImageUrl", "test.png");

        when(profileService.updateProfileImage("testuser", "test.png"))
            .thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = profileController.updateProfileImage(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(profileService, times(1)).updateProfileImage("testuser", "test.png");
    }

    // ============= changePassword Tests =============

    @Test
    @DisplayName("changePassword - Happy Flow: Should change password and return 200 OK")
    void changePassword_shouldChangePasswordSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("currentPassword", "oldPassword123");
        request.put("newPassword", "newPassword123");

        doNothing().when(profileService).changePassword("testuser", "oldPassword123", "newPassword123");

        // Act
        ResponseEntity<?> response = profileController.changePassword(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Password changed successfully");
        verify(profileService, times(1)).changePassword("testuser", "oldPassword123", "newPassword123");
    }

    @Test
    @DisplayName("changePassword - Unhappy Flow: Should return 400 for incorrect current password")
    void changePassword_shouldReturn400ForIncorrectPassword() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("currentPassword", "wrongPassword");
        request.put("newPassword", "newPassword123");

        doThrow(new IllegalArgumentException("Current password is incorrect"))
            .when(profileService).changePassword("testuser", "wrongPassword", "newPassword123");

        // Act
        ResponseEntity<?> response = profileController.changePassword(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Current password is incorrect");
    }

    @Test
    @DisplayName("changePassword - Unhappy Flow: Should return 500 for unexpected errors")
    void changePassword_shouldReturn500ForUnexpectedError() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("currentPassword", "oldPassword");
        request.put("newPassword", "newPassword123");

        doThrow(new RuntimeException("Database error"))
            .when(profileService).changePassword(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<?> response = profileController.changePassword(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal server error");
    }

    // ============= trackVisitedPage Tests =============

    @Test
    @DisplayName("trackVisitedPage - Happy Flow: Should track page and return 200 OK")
    void trackVisitedPage_shouldTrackPageSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("pagePath", "/forum/post");

        when(profileService.addVisitedPage("testuser", "/forum/post")).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = profileController.trackVisitedPage(testUser, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(profileService, times(1)).addVisitedPage("testuser", "/forum/post");
    }

    // ============= deleteAccount Tests =============

    @Test
    @DisplayName("deleteAccount - Happy Flow: Should delete account and return 200 OK")
    void deleteAccount_shouldDeleteAccountSuccessfully() {
        // Arrange
        doNothing().when(profileService).deleteAccount("testuser");

        // Act
        ResponseEntity<?> response = profileController.deleteAccount(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Account successfully deleted");
        verify(profileService, times(1)).deleteAccount("testuser");
    }

    @Test
    @DisplayName("deleteAccount - Unhappy Flow: Should return 500 for deletion errors")
    void deleteAccount_shouldReturn500ForDeletionError() {
        // Arrange
        doThrow(new RuntimeException("Email service error"))
            .when(profileService).deleteAccount("testuser");

        // Act
        ResponseEntity<?> response = profileController.deleteAccount(testUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).asString().contains("Error deleting account");
    }
}
