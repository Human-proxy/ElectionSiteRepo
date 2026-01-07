package nl.hva.dederdekamer.election_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import nl.hva.dederdekamer.election_backend.service.JwtService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom JWT authentication filter (no Spring Security).
 *
 * Responsibilities:
 *  - Allow OPTIONS (CORS preflight) requests to pass through.
 *  - Allow specific "public" endpoints (login, register, etc.) to bypass JWT validation.
 *  - For other /api/** endpoints:
 *      * validate Authorization header (Bearer <token>)
 *      * parse & verify JWT via {@link JwtService}
 *      * load {@link UserEntity} and ensure active
 *      * attach user to request (used by {@link nl.hva.dederdekamer.election_backend.security.CurrentUser})
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository users;

    public JwtRequestFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        String method = req.getMethod();

        // --- Allow all OPTIONS (CORS preflight) requests ---
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(req, res);
            return;
        }

      // --- Public endpoints ---
boolean isPublic =
        path.startsWith("/h2-console")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
     || path.equals("/api/auth/login")
     || path.equals("/api/auth/register")
     || path.equals("/api/verification/verify")
     || path.equals("/api/verification/resend")
     || path.startsWith("/api/quiz")
     || path.startsWith(("/api/government-news"))
     || path.equals("/api/password-reset/request")
     || path.startsWith("/api/password-reset/validate/")
     || path.equals("/api/password-reset/reset")
    || path.equals("/api/elections/TK2023/municipalities/winners")
    || (path.startsWith("/api/elections/TK2023/municipalities/")
    && path.endsWith("/results"))
     || path.startsWith(("/api/v1/tag/"))
     || (
            (HttpMethod.GET.matches(method) && (
                path.equals("/api/v1/posts") ||
                path.startsWith("/api/v1/posts/") ||
                path.equals("/api/v1/comments") ||
                path.startsWith("/api/v1/comments/") ||
                path.equals("/api/v1/parties/homepage") ||
                path.equals("/api/elections") ||
                path.startsWith("/api/elections/metadata/") ||
                path.startsWith("/api/elections/") && path.endsWith("/results") ||
                path.equals("/api/elections/compare") ||
                path.equals("/api/party-data") ||
                path.equals("/api/v1/election/top4") ||
                path.startsWith("/api/v1/elections/metadata/")
            ))
        )
     || (
            (HttpMethod.DELETE.matches(method) && (
                path.equals("/api/v1/comment") ||
                path.startsWith("/api/v1/comment/")
            ))
        );

        if (isPublic) {
            // ⚠️ TODO: Restrict public access for /api/posts in production
            chain.doFilter(req, res);
            return;
        }

        // --- Protected endpoints (require JWT) ---
        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            unauthorized(res, "Missing Bearer token");
            return;
        }

        String token = header.substring(7);
        try {
            String username = jwt.validateAndGetUsername(token);
            UserEntity user = users.findByUsername(username).orElse(null);

            // Check for soft delete (allow access if within 30 days)
            boolean isSoftDeleted = user != null && user.getDeletedAt() != null && 
                                    user.getDeletedAt().isAfter(java.time.LocalDateTime.now().minusDays(30));

            if (user == null || (!isSoftDeleted && !user.isEnabled())) {
                unauthorized(res, "User not found or disabled");
                return;
            }

            // If soft deleted, only allow access to reactivation endpoint
            if (isSoftDeleted && !path.equals("/api/auth/reactivate")) {
                unauthorized(res, "Account is deactivated");
                return;
            }

            // Attach user to request for @CurrentUser resolver
            req.setAttribute("currentUser", user);
            chain.doFilter(req, res);

        } catch (Exception e) {
            unauthorized(res, "Invalid or expired token");
        }
    }

    /**
     * Writes a simple JSON 401 response.
     */
    private void unauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + msg + "\"}");
    }
}
