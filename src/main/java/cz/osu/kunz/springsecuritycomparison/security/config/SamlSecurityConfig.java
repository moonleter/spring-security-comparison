package cz.osu.kunz.springsecuritycomparison.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.*;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("saml")
public class SamlSecurityConfig {

    private PrivateKey loadPrivateKey(ClassPathResource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

    private X509Certificate loadCertificate(ClassPathResource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        }
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository(
            @Value("${KEYCLOAK_URL:http://keycloak:8080}") String internalUrl,
            @Value("${KEYCLOAK_EXTERNAL_URL:http://localhost:8081}") String externalUrl) throws Exception {

        PrivateKey privateKey = loadPrivateKey(new ClassPathResource("credentials/saml-private.key"));
        X509Certificate certificate = loadCertificate(new ClassPathResource("credentials/saml-public.crt"));

        Saml2X509Credential signingCredential = new Saml2X509Credential(
                privateKey, certificate, Saml2X509Credential.Saml2X509CredentialType.SIGNING);

        RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations
                .fromMetadataLocation(internalUrl + "/realms/osu/protocol/saml/descriptor");

        builder.registrationId("keycloak");
        builder.entityId("spring-boot-saml-client");

        builder.assertingPartyMetadata(party -> {
            party.entityId(externalUrl + "/realms/osu");
            party.singleSignOnServiceLocation(externalUrl + "/realms/osu/protocol/saml");
        });

        builder.signingX509Credentials(c -> c.add(signingCredential));

        return new InMemoryRelyingPartyRegistrationRepository(builder.build());
    }

    @Bean
    public OpenSaml5AuthenticationProvider samlAuthenticationProvider() {
        OpenSaml5AuthenticationProvider provider = new OpenSaml5AuthenticationProvider();

        Converter<OpenSaml5AuthenticationProvider.ResponseToken, Saml2Authentication> delegate =
                OpenSaml5AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        provider.setResponseAuthenticationConverter(responseToken -> {

            Saml2AssertionAuthentication authentication = (Saml2AssertionAuthentication) delegate.convert(responseToken);

            Saml2ResponseAssertionAccessor assertionAccessor = authentication.getCredentials();
            Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

            if (assertionAccessor != null) {
                List<Object> roles = assertionAccessor.getAttribute("roles");
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase())));
                }
            }

            return new Saml2AssertionAuthentication(
                    authentication.getPrincipal(),
                    assertionAccessor,
                    authorities,
                    authentication.getRelyingPartyRegistrationId()
            );
        });

        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/notes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/notes/**").authenticated()
                        .anyRequest().authenticated()
                )
                .saml2Login(saml2 -> saml2
                        .authenticationManager(new ProviderManager(samlAuthenticationProvider()))
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                );

        return http.build();
    }
}