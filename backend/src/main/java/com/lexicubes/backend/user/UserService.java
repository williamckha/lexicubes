package com.lexicubes.backend.user;

import com.lexicubes.backend.score.ScoreRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final ScoreRepository scoreRepository;

    public UserService(UserRepository userRepository, ScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
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

    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            scoreRepository.deleteAllScoresByUserId(userId);
            userRepository.deleteById(userId);
        }
    }
}
