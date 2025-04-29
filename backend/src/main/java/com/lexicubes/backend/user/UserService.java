package com.lexicubes.backend.user;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuth2User(OAuth2User oAuth2User, String provider) {
        final String providerId = oAuth2User.getName();
        final String email = oAuth2User.getAttribute("email");
        final String name = oAuth2User.getAttribute("name");

        final User user = userRepository
                .findUserByProviderAndProviderId(provider, providerId)
                .orElse(User.of(email, name, provider, providerId));

        return userRepository.save(user);
    }
}
