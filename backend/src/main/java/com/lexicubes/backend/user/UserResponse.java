package com.lexicubes.backend.user;

import org.jetbrains.annotations.Nullable;

public record UserResponse(Long id,
                           @Nullable String email,
                           String name,
                           String provider) {
}
