package nl.hva.dederdekamer.election_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "StemWijs Election API",
        version = "1.0",
        description = "REST API for Dutch Election Platform - provides authentication, user profiles, election data, quiz functionality, and community features",
        contact = @Contact(
            name = "De Derde Kamer Team",
            email = "support@stemwijs.nl"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api.stemwijs.nl", description = "Production Server")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Enter your JWT token obtained from /api/auth/login"
)
public class OpenApiConfig {
    // Configuration is handled via annotations
}
