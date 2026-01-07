package nl.hva.dederdekamer.election_backend.security;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a controller parameter to inject the authenticated UserEntity.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface CurrentUser {}
