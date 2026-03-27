package com.ythwork.authorizationserver.security.password;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PasswordGrantAuthenticationConverter implements AuthenticationConverter {

    private static final Logger log = LoggerFactory.getLogger(PasswordGrantAuthenticationConverter.class);

    private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:password";

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter("grant_type");

        if (!GRANT_TYPE.equals(grantType)) {
            return null;
        }

        log.debug("Password grant request received");

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (!(clientPrincipal instanceof OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken)) {
            log.warn("Client authentication missing or invalid for password grant");
            return null;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String scope = request.getParameter("scope");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(scope)) {
            return null;
        }

        Set<String> scopes = new HashSet<>();
        if (StringUtils.hasText(scope)) {
            scopes.addAll(Arrays.asList(scope.split(" ")));
        }

        log.info("Password grant authentication attempt: username={}, scopes={}",
                username,
                scopes);

        return new PasswordGrantAuthenticationToken(
                oAuth2ClientAuthenticationToken,
                username,
                password,
                scopes,
                Collections.emptyMap());
    }
}
