package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Admin user service providing user listing and deletion operations.
 */
@Service
public class AdminUserService {

    private final UserRepository users;

    public AdminUserService(UserRepository users) {
        this.users = users;
    }

    /** Returns all users mapped to {@link UserResponse}. */
    public List<UserResponse> listUsers() {
        return users.findAll().stream().map(this::map).toList();
    }

    /** Deletes a user by id, throws 404 if missing. */
    public void deleteUser(Long id) {
        var user = users.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        users.delete(user);
    }

    /** Maps a {@link UserEntity} to {@link UserResponse}. */
    private UserResponse map(UserEntity u) {
        Set<String> roleNames = u.getRoles() == null ? Set.of() :
            u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());
        return new UserResponse(
            String.valueOf(u.getId()),
            u.getUsername(),
            u.getEmail(),
            u.getProfileImageUrl(),
            u.getCreatedAt(),
            u.getVisitedPages(),
            roleNames, null
        );
    }
}

