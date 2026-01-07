package nl.hva.dederdekamer.election_backend;

import nl.hva.dederdekamer.election_backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Generates a JWT for manual testing of the security filter.
 * Run this test and copy the printed token to call protected endpoints.
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtSmokeTest {

    @Autowired
    JwtService jwt;

    @Test
    void printToken() {
        String token = jwt.generate("aydin"); // any username string
        System.out.println("\n=== DEV TOKEN (copy below) ===\nBearer " + token + "\n");
    }
}
