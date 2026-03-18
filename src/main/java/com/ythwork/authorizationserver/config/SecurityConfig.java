package com.ythwork.authorizationserver.config;

import com.ythwork.authorizationserver.security.password.PasswordGrantAuthenticationConverter;
import com.ythwork.authorizationserver.security.password.PasswordGrantAuthenticationProvider;
import com.ythwork.authorizationserver.security.password.PasswordGrantAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Value("${authorization.server.issuer}")
    private String issuer;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   OAuth2AuthorizationService authorizationService,
                                                   OAuth2TokenGenerator<?> tokenGenerator) throws Exception {
        OAuth2AuthorizationServerConfigurer auth2AuthorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.securityMatcher(auth2AuthorizationServerConfigurer.getEndpointsMatcher())
                .with(auth2AuthorizationServerConfigurer, configurer -> configurer
                        .tokenEndpoint(token -> token
                                .accessTokenRequestConverter(new PasswordGrantAuthenticationConverter())
                                .authenticationProvider(new PasswordGrantAuthenticationProvider(
                                        authorizationService,
                                        tokenGenerator,
                                        authenticationManager
                                ))));

        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/token"));

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientSecret("{noop}secret")
                .authorizationGrantType(PasswordGrantAuthenticationToken.PASSWORD_GRANT_TYPE)
                .scope("read")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}
