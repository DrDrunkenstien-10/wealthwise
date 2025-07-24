package com.wealthwise.user.service;

import java.util.Optional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        String keycloakId = jwt.getSubject();

        if (username == null || keycloakId == null) {
            throw new IllegalArgumentException("Missing required JWT claims");
        }

        Optional<User> existingUser = userRepository.findByUsernameAndKeycloakId(username, keycloakId);

        if (existingUser.isPresent()) {
            return false; 
        }

        User user = new User();
        user.setUsername(username);
        user.setKeycloakId(keycloakId);
        user.setEmail(jwt.getClaim("email"));
        userRepository.save(user);

        return true;
    }
}
