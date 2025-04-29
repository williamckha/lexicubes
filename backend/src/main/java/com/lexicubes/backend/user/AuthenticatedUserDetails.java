package com.lexicubes.backend.user;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public abstract class AuthenticatedUserDetails {

    private final Long id;

    private final String email;

    private final String username;

    private final String provider;

    private final Set<? extends GrantedAuthority> authorities;

    public AuthenticatedUserDetails(Long id,
                                    String email,
                                    String username,
                                    String provider,
                                    Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.provider = provider;
        this.authorities = Set.copyOf(authorities);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getProvider() {
        return provider;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
