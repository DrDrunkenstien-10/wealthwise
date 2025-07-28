package com.wealthwise.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import com.wealthwise.transaction.exception.UserNotFoundException;
import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;
import com.wealthwise.user.service.CurrentUserProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class CurrentUserProviderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CurrentUserProvider currentUserProvider;

    private final String keycloakId = "abc-123";
    private final String username = "john_doe";

    @BeforeEach
    void setUp() {
        when(jwt.getSubject()).thenReturn(keycloakId);
        when(jwt.getClaim("preferred_username")).thenReturn(username);
    }

    @Test
    void shouldReturnUserIdWhenUserExists() {
        UUID expectedUserId = UUID.randomUUID();
        User user = new User();
        user.setUserId(expectedUserId);

        when(userRepository.findByUsernameAndKeycloakId(username, keycloakId))
                .thenReturn(Optional.of(user));

        UUID actualUserId = currentUserProvider.getCurrentUserId(jwt);

        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsernameAndKeycloakId(username, keycloakId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            currentUserProvider.getCurrentUserId(jwt);
        });
    }
}