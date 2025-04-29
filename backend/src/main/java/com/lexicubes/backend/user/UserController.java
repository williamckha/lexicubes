package com.lexicubes.backend.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user")
    public UserResponse getUser(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return new UserResponse(
                principal.getId(),
                principal.getEmail(),
                principal.getUsername(),
                principal.getProvider());
    }
}
