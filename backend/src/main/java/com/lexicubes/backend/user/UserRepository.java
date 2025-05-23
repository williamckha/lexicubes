package com.lexicubes.backend.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByProviderAndProviderId(String provider, String providerId);
}
