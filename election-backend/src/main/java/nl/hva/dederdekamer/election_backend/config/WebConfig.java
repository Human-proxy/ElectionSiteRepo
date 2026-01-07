package nl.hva.dederdekamer.election_backend.config;

import nl.hva.dederdekamer.election_backend.security.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Registers the @CurrentUser argument resolver with Spring MVC.
 *
 * This allows controllers to declare:
 *   public ResponseEntity<?> me(@CurrentUser UserEntity user)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final CurrentUserArgumentResolver resolver;
  public WebConfig(CurrentUserArgumentResolver resolver) { this.resolver = resolver; }

  @Override public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(resolver);
  }
}