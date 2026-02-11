package cz.osu.kunz.springsecuritycomparison.service.helper;


import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProtocolResolver {

    private final Environment environment;

    public String getCurrentProtocol() {
        String[] profiles = environment.getActiveProfiles();

        return Arrays.stream(profiles)
                .filter(p -> p.equals("ldap") || p.equals("saml") || p.equals("oidc"))
                .findFirst()
                .map(String::toUpperCase)
                .orElse("UNKNOWN");
    }
}