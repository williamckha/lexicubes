package com.lexicubes.backend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GoogleAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_AUTH_SCHEME = "Bearer ";
    private static final String SERVICE_ACCOUNT_EMAIL_DOMAIN = "lexicubes-game.iam.gserviceaccount.com";

    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthenticationFilter(@Value("${app.backend.url}") String backendUrl)
            throws GeneralSecurityException, IOException {

        verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(backendUrl))
                .build();
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        final Optional<String> bearerToken = getBearerToken(request);
        if (bearerToken.isPresent()) {
            try {
                final GoogleIdToken idToken = verifier.verify(bearerToken.get());
                if (idToken != null) {
                    final String email = idToken.getPayload().getEmail();
                    if (email != null && email.endsWith(SERVICE_ACCOUNT_EMAIL_DOMAIN)) {
                        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        final Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (GeneralSecurityException e) {
                response.sendError(HttpStatus.FORBIDDEN.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getBearerToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (authHeader != null && authHeader.toLowerCase().startsWith(BEARER_AUTH_SCHEME.toLowerCase())) {
            return Optional.of(authHeader.substring(BEARER_AUTH_SCHEME.length()));
        }
        return Optional.empty();
    }
}
