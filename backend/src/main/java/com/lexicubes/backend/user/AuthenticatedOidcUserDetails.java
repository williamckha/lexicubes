package com.lexicubes.backend.user;

import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class AuthenticatedOidcUserDetails extends AuthenticatedOAuth2UserDetails implements OidcUser {

    private final OidcUserInfo oidcUserInfo;

    private final OidcIdToken oidcIdToken;

    public AuthenticatedOidcUserDetails(Long id,
                                        String email,
                                        String username,
                                        String provider,
                                        Collection<? extends GrantedAuthority> authorities,
                                        Map<String, Object> attributes,
                                        String nameAttributeKey,
                                        OidcUserInfo oidcUserInfo,
                                        OidcIdToken oidcIdToken) {

        super(id, email, username, provider, authorities, attributes, nameAttributeKey);

        this.oidcUserInfo = oidcUserInfo;
        this.oidcIdToken = oidcIdToken;
    }

    @Override
    public Map<String, Object> getClaims() {
        return getAttributes();
    }

    @Override
    public @Nullable OidcUserInfo getUserInfo() {
        return oidcUserInfo;
    }

    @Override
    public @Nullable OidcIdToken getIdToken() {
        return oidcIdToken;
    }
}
