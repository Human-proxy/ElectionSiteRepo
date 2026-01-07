package nl.hva.dederdekamer.election_backend.profile;

import nl.hva.dederdekamer.election_backend.dto.ProfileUpdateResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.RoleEntity;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.model.RoleName;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.EmailService;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import nl.hva.dederdekamer.election_backend.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProfileService using Mockito.
 * Tests both happy and unhappy flows for profile management.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Tests")
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProfileService profileService;

    private UserEntity testUser;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userRole = new RoleEntity(RoleName.USER);
        testUser = new UserEntity(
            "testuser",
            "test@example.com",
            "hashedPassword",
            true,
            new HashSet<>(Set.of(userRole))
        );
    }

    // ============= getProfile Tests =============

    @Test
    @DisplayName("getProfile - Happy Flow: Should return user profile")
    void getProfile_shouldReturnUserProfile() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = profileService.getProfile("testuser");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("getProfile - Unhappy Flow: Should throw exception when user not found")
    void getProfile_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profileService.getProfile("nonexistent"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    // ============= updateProfileImage Tests =============

    @Test
    @DisplayName("updateProfileImage - Happy Flow: Should update profile image")
    void updateProfileImage_shouldUpdateImage() {
        // Arrange
        String newImageUrl = "https://example.com/new-image.jpg";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        UserResponse response = profileService.updateProfileImage("testuser", newImageUrl);

        // Assert
        assertThat(response).isNotNull();
        assertThat(testUser.getProfileImageUrl()).isEqualTo(newImageUrl);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("updateProfileImage - Unhappy Flow: Should throw exception when user not found")
    void updateProfileImage_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profileService.updateProfileImage("nonexistent", "image-url"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    // ============= updateProfile Tests =============

    @Test
    @DisplayName("updateProfile - Happy Flow: Should update username and email")
    void updateProfile_shouldUpdateUsernameAndEmail() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
        when(jwtService.generate("newuser")).thenReturn("new-jwt-token");

        // Act
        ProfileUpdateResponse response = profileService.updateProfile("testuser", "newuser", "newemail@example.com");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-jwt-token");
        assertThat(testUser.getUsername()).isEqualTo("newuser");
        assertThat(testUser.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("updateProfile - Unhappy Flow: Should throw exception when username already exists")
    void updateProfile_shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        RoleEntity role = new RoleEntity(RoleName.USER);
        UserEntity existingUser = new UserEntity("existinguser", "existing@test.com", "hash", true, new HashSet<>(Set.of(role)));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> profileService.updateProfile("testuser", "existinguser", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username already exists");
    }

    @Test
    @DisplayName("updateProfile - Unhappy Flow: Should throw exception when email already exists")
    void updateProfile_shouldThrowExceptionWhenEmailExists() {
        // Arrange
        RoleEntity role = new RoleEntity(RoleName.USER);
        UserEntity existingUser = new UserEntity("existinguser", "existing@test.com", "hash", true, new HashSet<>(Set.of(role)));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> profileService.updateProfile("testuser", null, "existing@example.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email already exists");
    }

    @Test
    @DisplayName("updateProfile - Unhappy Flow: Should throw exception for invalid email format")
    void updateProfile_shouldThrowExceptionForInvalidEmail() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> profileService.updateProfile("testuser", null, "invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email format");
    }

    // ============= addVisitedPage Tests =============

    @Test
    @DisplayName("addVisitedPage - Happy Flow: Should add visited page")
    void addVisitedPage_shouldAddPage() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        UserResponse response = profileService.addVisitedPage("testuser", "/leer/grondwet");

        // Assert
        assertThat(response).isNotNull();
        assertThat(testUser.getVisitedPages()).contains("/leer/grondwet");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("addVisitedPage - Unhappy Flow: Should throw exception when user not found")
    void addVisitedPage_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profileService.addVisitedPage("nonexistent", "/page"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }

    // ============= changePassword Tests =============

    @Test
    @DisplayName("changePassword - Happy Flow: Should change password successfully")
    void changePassword_shouldChangePassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "hashedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        profileService.changePassword("testuser", "oldPassword", "newPassword123");

        // Assert
        assertThat(testUser.getPasswordHash()).isEqualTo("newHashedPassword");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("changePassword - Unhappy Flow: Should throw exception for incorrect current password")
    void changePassword_shouldThrowExceptionForIncorrectPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> profileService.changePassword("testuser", "wrongPassword", "newPassword123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Current password is incorrect");
    }

    @Test
    @DisplayName("changePassword - Unhappy Flow: Should throw exception for short password")
    void changePassword_shouldThrowExceptionForShortPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> profileService.changePassword("testuser", "oldPassword", "short"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("New password must be at least 8 characters long");
    }

    @Test
    @DisplayName("changePassword - Unhappy Flow: Should throw exception when new password equals current")
    void changePassword_shouldThrowExceptionWhenPasswordSame() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("samePassword", "hashedPassword")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> profileService.changePassword("testuser", "samePassword", "samePassword"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("New password cannot be the same as the current password");
    }

    // ============= deleteAccount Tests =============

    @Test
    @DisplayName("deleteAccount - Happy Flow: Should soft delete account")
    void deleteAccount_shouldSoftDeleteAccount() throws Exception {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
        doNothing().when(emailService).sendAccountDeactivationEmail(anyString(), anyString());

        // Act
        profileService.deleteAccount("testuser");

        // Assert
        assertThat(testUser.getDeletedAt()).isNotNull();
        assertThat(testUser.isEnabled()).isFalse();
        verify(userRepository, times(1)).save(testUser);
        verify(emailService, times(1)).sendAccountDeactivationEmail("test@example.com", "testuser");
    }

    @Test
    @DisplayName("deleteAccount - Unhappy Flow: Should throw exception when user not found")
    void deleteAccount_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profileService.deleteAccount("nonexistent"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");
    }
}
