package cz.osu.kunz.springsecuritycomparison.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
//TODO: change to switch
        // 1. OIDC (OAuth2)
        if (auth instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
            return (String) jwtAuth.getTokenAttributes().getOrDefault("preferred_username", auth.getName());
        } else if (auth instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauth2Auth) {
            return (String) oauth2Auth.getPrincipal().getAttributes().getOrDefault("preferred_username", auth.getName());
        }
        // 2. SAML 2.0
        else if (auth instanceof org.springframework.security.saml2.provider.service.authentication.Saml2Authentication samlAuth) {
            return samlAuth.getName(); // Spring automaticky vytáhne NameID
        }

        // 3. LDAP
        return auth.getName();
    }

    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));
        //TODO: maybe change the roleName if It is named somehow differently
    }
}