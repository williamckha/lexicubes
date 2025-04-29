package com.lexicubes.backend.user;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {

    @Id
    @Nullable
    private final Long id;

    @Nullable
    private final String email;

    private final String name;

    private final String provider;

    private final String providerId;

    public static User of(@Nullable String email,
                          String name,
                          String provider,
                          String providerId) {

        return new User(null, email, name, provider, providerId);
    }

    public User(@Nullable Long id,
                @Nullable String email,
                String name,
                String provider,
                String providerId) {

        this.id = id;
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
    }

    public @Nullable Long getId() {
        return id;
    }

    public @Nullable String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }
}
