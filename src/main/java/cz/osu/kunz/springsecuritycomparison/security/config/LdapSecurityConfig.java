package cz.osu.kunz.springsecuritycomparison.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("ldap")
public class LdapSecurityConfig {

    @Value("${spring.ldap.urls}")
    private String ldapUrl;
    @Value("${spring.ldap.base}")
    private String ldapBase;
    @Value("${spring.ldap.username}")
    private String ldapUsername;
    @Value("${spring.ldap.password}")
    private String ldapPassword;
    @Value("${app.security.ldap.user-search-base}")
    private String userSearchBase;
    @Value("${app.security.ldap.user-search-filter}")
    private String userSearchFilter;
    @Value("${app.security.ldap.group-search-base}")
    private String groupSearchBase;
    @Value("${app.security.ldap.group-search-filter}")
    private String groupSearchFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/notes/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll()
                );

        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userSearchBase(userSearchBase)
                .userSearchFilter(userSearchFilter)
                .groupSearchBase(groupSearchBase)
                .groupSearchFilter(groupSearchFilter)
                .contextSource()
                .url(ldapUrl + "/" + ldapBase)
                .managerDn(ldapUsername)
                .managerPassword(ldapPassword);
    }
}