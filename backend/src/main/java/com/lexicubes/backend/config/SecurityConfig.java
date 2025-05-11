package com.lexicubes.backend.config;

import com.lexicubes.backend.user.AuthenticatedOAuth2UserDetails;
import com.lexicubes.backend.user.AuthenticatedOidcUserDetails;
import com.lexicubes.backend.user.User;
import com.lexicubes.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   GoogleAuthenticationFilter googleAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/user").authenticated()
                        .requestMatchers("/api/puzzles/*/score").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())
                                .oidcUserService(oidcUserService())
                        )
                        .defaultSuccessUrl(frontendUrl, true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl(frontendUrl)
                )
                .addFilterAfter(googleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return request -> {
            final OAuth2User oAuth2User = delegate.loadUser(request);

            final String provider = request.getClientRegistration().getRegistrationId();
            final String nameAttributeKey = request.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            final User user = userService.processOAuth2User(oAuth2User, provider);

            return new AuthenticatedOAuth2UserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getProvider(),
                    oAuth2User.getAuthorities(),
                    oAuth2User.getAttributes(),
                    nameAttributeKey);
        };
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return request -> {
            final OidcUser oidcUser = delegate.loadUser(request);

            final String provider = request.getClientRegistration().getRegistrationId();
            final String nameAttributeKey = request.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            final User user = userService.processOAuth2User(oidcUser, provider);

            return new AuthenticatedOidcUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getProvider(),
                    oidcUser.getAuthorities(),
                    oidcUser.getAttributes(),
                    nameAttributeKey,
                    oidcUser.getUserInfo(),
                    oidcUser.getIdToken());
        };
    }
}
