package nl.hva.dederdekamer.election_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import nl.hva.dederdekamer.election_backend.exception.UnauthorizedException;
import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
/**
 * Resolves @CurrentUser parameters from the "currentUser" request attribute
 * set by JwtRequestFilter. Throws 401 if missing.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && UserEntity.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {

        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        Object user = (req != null) ? req.getAttribute("currentUser") : null;

        if (user == null) {
            // No authenticated user found; reject the request
            throw new UnauthorizedException("Login required");
        }

        return user;
    }
}