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

        //TODO: impl later when implementing each protocols
        // OIDC (OAuth2)
//        if (auth instanceof JwtAuthenticationToken jwtAuth) {
//            // Keycloak ukládá čitelné uživatelské jméno do claimu "preferred_username"
//            return (String) jwtAuth.getTokenAttributes().getOrDefault("preferred_username", auth.getName());
//        }
//        //  SAML
//        else if (auth instanceof Saml2Authentication samlAuth) {
//            return samlAuth.getName();
//        }

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