package cz.osu.kunz.springsecuritycomparison.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {

    /**
     * @return Returns current username based on the authentication type. It checks for JWT, OAuth2, and SAML.
     */
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        return switch (auth) {
            case org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth ->
                    (String) jwtAuth.getTokenAttributes().getOrDefault("preferred_username", auth.getName());
            case org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauth2Auth ->
                    (String) oauth2Auth.getPrincipal().getAttributes().getOrDefault("preferred_username", auth.getName());
            case org.springframework.security.saml2.provider.service.authentication.Saml2Authentication samlAuth ->
                    samlAuth.getName();
            default -> auth.getName();
        };
    }


    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));
    }
}