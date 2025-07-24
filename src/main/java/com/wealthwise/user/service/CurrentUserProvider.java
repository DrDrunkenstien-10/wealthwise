package com.wealthwise.user.service;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;

@Service
public class CurrentUserProvider {
    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID getCurrentUserId(Jwt jwt) {
        String keycloakId = jwt.getSubject(); // "sub"
        String username = jwt.getClaim("preferred_username");

        return userRepository.findByUsernameAndKeycloakId(username, keycloakId)
                .map(User::getUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
