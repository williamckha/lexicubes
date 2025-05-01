package com.lexicubes.backend.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user")
    public UserResponse getUser(@AuthenticationPrincipal AuthenticatedUserDetails principal) {
        return new UserResponse(
                principal.getId(),
                principal.getEmail(),
                principal.getUsername(),
                principal.getProvider());
    }

    @DeleteMapping("/api/user")
    public void deleteUser(@AuthenticationPrincipal AuthenticatedUserDetails principal,
                           Authentication authentication,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        userService.deleteUser(principal.getId());

        final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
    }
}
