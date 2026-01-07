package nl.hva.dederdekamer.election_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.hva.dederdekamer.election_backend.dto.ProfileUpdateResponse;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.security.CurrentUser;
import nl.hva.dederdekamer.election_backend.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * Controller for user profile management.
 *
 * Endpoints:
 *  GET  /api/v1/profile — get current user's profile
 *  PUT  /api/v1/profile — update user profile (username, email)
 *  PUT  /api/v1/profile/image — update profile image
 *  PUT  /api/v1/profile/password — change password
 *  POST /api/v1/profile/visit — track visited page
 */
@Tag(name = "Profile Management", description = "Endpoints for managing user profile information")
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Returns the current authenticated user's profile.
     * @param currentUser injected from JwtRequestFilter via @CurrentUser
     */
    @Operation(summary = "Get user profile", description = "Retrieves the current authenticated user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved profile",
                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@CurrentUser UserEntity currentUser) {
        return ResponseEntity.ok(profileService.getProfile(currentUser.getUsername()));
    }

    /**
     * Updates the current authenticated user's profile information.
     * If username is changed, a new JWT token is returned in the response.
     *
     * @param authentication current authenticated user
     * @param request map containing "username" and/or "email" keys
     * @return ProfileUpdateResponse with updated user data and optionally new token
     */
    @Operation(summary = "Update user profile", description = "Updates username and/or email. Returns new JWT if username changes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                     content = @Content(schema = @Schema(implementation = ProfileUpdateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping
    public ResponseEntity<?> updateProfile(@CurrentUser UserEntity currentUser,
                                           @RequestBody Map<String, String> request) {
        try {
            String newUsername = request.get("username");
            String newEmail    = request.get("email");
            ProfileUpdateResponse response =
                    profileService.updateProfile(currentUser.getUsername(), newUsername, newEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    /**
     * Updates the profile image URL for the current authenticated user.
     *
     * @param authentication current authenticated user
     * @param request map containing "profileImageUrl" key
     * @return updated user response
     */
    @Operation(summary = "Update profile image", description = "Updates the user's profile image URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile image updated successfully",
                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/image")
    public ResponseEntity<UserResponse> updateProfileImage(@CurrentUser UserEntity currentUser,
                                                           @RequestBody Map<String, String> request) {
        String profileImageUrl = request.get("profileImageUrl");
        return ResponseEntity.ok(profileService.updateProfileImage(currentUser.getUsername(), profileImageUrl));
    }

    /**
     * Changes the password for the current authenticated user.
     *
     * @param authentication current authenticated user
     * @param request map containing "currentPassword" and "newPassword" keys
     * @return success or error message
     */
    @Operation(summary = "Change password", description = "Changes the user's password after validating current password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid current password or validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@CurrentUser UserEntity currentUser,
                                            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword     = request.get("newPassword");
            profileService.changePassword(currentUser.getUsername(), currentPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    /**
     * Tracks a visited page for the current authenticated user.
     *
     * @param authentication current authenticated user
     * @param request map containing "pagePath" key
     * @return updated user response
     */
    @Operation(summary = "Track visited page", description = "Adds a page to the user's visited pages history for progress tracking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page visit tracked successfully",
                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/visit")
    public ResponseEntity<UserResponse> trackVisitedPage(@CurrentUser UserEntity currentUser,
                                                         @RequestBody Map<String, String> request) {
        String pagePath = request.get("pagePath");
        return ResponseEntity.ok(profileService.addVisitedPage(currentUser.getUsername(), pagePath));
    }



    /**
     * Deletes the current authenticated user's account.
     *
     * @param authentication current authenticated user
     * @return success message
     */
    @Operation(summary = "Delete account", description = "Permanently deletes the user's account and all associated data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Error during account deletion")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping
    public ResponseEntity<?> deleteAccount(@CurrentUser UserEntity currentUser) {
        try {
            profileService.deleteAccount(currentUser.getUsername());
            return ResponseEntity.ok("Account successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting account: " + e.getMessage());
        }
    }
}
