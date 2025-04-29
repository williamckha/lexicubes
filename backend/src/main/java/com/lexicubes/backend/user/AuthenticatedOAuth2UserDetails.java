package com.lexicubes.backend.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class AuthenticatedOAuth2UserDetails extends AuthenticatedUserDetails implements OAuth2User {

    private final Map<String, Object> attributes;

    private final String nameAttributeKey;

    public AuthenticatedOAuth2UserDetails(Long id,
                                          String email,
                                          String username,
                                          String provider,
                                          Collection<? extends GrantedAuthority> authorities,
                                          Map<String, Object> attributes,
                                          String nameAttributeKey) {

        super(id, email, username, provider, authorities);

        if (nameAttributeKey.isEmpty()) {
            throw new IllegalArgumentException("nameAttributeKey cannot be empty");
        }

        if (attributes.get(nameAttributeKey) == null) {
            throw new IllegalArgumentException(nameAttributeKey + " attribute cannot be null");
        }

        // Avoiding Map.copyOf() since attributes may have null values, which are only allowed by HashMap
        this.attributes = new HashMap<>(attributes);
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(getAttribute(nameAttributeKey)).toString();
    }
}
