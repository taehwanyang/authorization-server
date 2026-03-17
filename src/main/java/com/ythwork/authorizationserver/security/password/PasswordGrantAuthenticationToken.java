package com.ythwork.authorizationserver.security.password;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PasswordGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    public static final AuthorizationGrantType PASSWORD_GRANT_TYPE = new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:password");

    private final String username;
    private final String password;
    private final Set<String> scopes;

    public PasswordGrantAuthenticationToken(
            OAuth2ClientAuthenticationToken clientPrincipal,
            String username,
            String password,
            Set<String> scopes,
            Map<String, Object> additionalParameters) {
        super(PASSWORD_GRANT_TYPE, clientPrincipal, additionalParameters);
        this.username = username;
        this.password = password;
        this.scopes = scopes == null ? Collections.emptySet() : scopes;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public Set<String> getScopes() {
        return scopes;
    }
}
