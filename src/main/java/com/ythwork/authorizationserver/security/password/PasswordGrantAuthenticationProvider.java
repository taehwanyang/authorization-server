package com.ythwork.authorizationserver.security.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Set;

public class PasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(PasswordGrantAuthenticationProvider.class);

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<?> tokenGenerator;
    private final AuthenticationManager authenticationManager;

    public PasswordGrantAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                               OAuth2TokenGenerator<?> tokenGenerator,
                                               AuthenticationManager authenticationManager) {
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PasswordGrantAuthenticationToken token = (PasswordGrantAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken client = (OAuth2ClientAuthenticationToken) token.getPrincipal();
        RegisteredClient registeredClient = client.getRegisteredClient();

        String username = token.getUsername();
        Set<String> scopes = token.getScopes();

        log.info("Password grant authentication started: username={}, scopes={}",
                username, scopes);

        Authentication userAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(token.getUsername(), token.getPassword()));

        if (!userAuthentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("User authentication success: username={}", username);

        DefaultOAuth2TokenContext context = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizationGrantType(PasswordGrantAuthenticationToken.PASSWORD_GRANT_TYPE)
                .authorizationGrant(token)
                .authorizedScopes(scopes)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .build();

        OAuth2Token oAuth2Token = tokenGenerator.generate(context);

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                oAuth2Token.getTokenValue(),
                oAuth2Token.getIssuedAt(),
                oAuth2Token.getExpiresAt(),
                scopes);

        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(userAuthentication.getName())
                .authorizationGrantType(PasswordGrantAuthenticationToken.PASSWORD_GRANT_TYPE)
                .accessToken(oAuth2AccessToken)
                .build();

        authorizationService.save(authorization);

        log.info("Access token issued: username={}, expiresAt={}",
                username, oAuth2AccessToken.getExpiresAt());

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, client, oAuth2AccessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
