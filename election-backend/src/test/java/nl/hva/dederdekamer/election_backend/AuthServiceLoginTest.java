package nl.hva.dederdekamer.election_backend;

import nl.hva.dederdekamer.election_backend.dto.JwtResponse;
import nl.hva.dederdekamer.election_backend.dto.LoginRequest;
import nl.hva.dederdekamer.election_backend.dto.RegisterRequest;
import nl.hva.dederdekamer.election_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthServiceLoginTest {

    @Autowired private AuthService auth;

    private RegisterRequest reg(String username, String email) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername(username);
        r.setEmail(email);
        r.setPassword("Password123!");
        r.setConfirmPassword("Password123!");
        return r;
    }

    private LoginRequest login(String identifier, String password) {
        LoginRequest l = new LoginRequest();
        l.setIdentifier(identifier);
        l.setPassword(password);
        return l;
    }

    @BeforeEach
    void setupUser() {
        auth.register(reg("aydin", "aydin@gmail.com"));
    }

    @Test @DisplayName("login by username should return JWT")
    void loginByUsername() {
        JwtResponse res = auth.login(login("aydin", "Password123!"));
        assertThat(res.getToken()).isNotBlank();
        assertThat(res.getUser().getUsername()).isEqualTo("aydin");
    }

    @Test @DisplayName("login by email should return JWT")
    void loginByEmail() {
        JwtResponse res = auth.login(login("AYDIN@gmail.COM", "Password123!")); // case-insensitive
        assertThat(res.getToken()).isNotBlank();
        assertThat(res.getUser().getEmail()).isEqualTo("aydin@gmail.com");
    }
}
