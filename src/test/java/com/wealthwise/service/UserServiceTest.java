package com.wealthwise.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.wealthwise.user.model.User;
import com.wealthwise.user.repository.UserRepository;
import com.wealthwise.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserService userService;

    private final String username = "john_doe";
    private final String keycloakId = "abc-123";
    private final String email = "john@example.com";

    @BeforeEach
    void setUp() {
        when(jwt.getClaim("preferred_username")).thenReturn(username);
        when(jwt.getSubject()).thenReturn(keycloakId);
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsMissing() {
        when(jwt.getClaim("preferred_username")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(jwt);
        });

        assertEquals("Missing required JWT claims", ex.getMessage());
    }

    @Test
    void shouldReturnFalseWhenUserAlreadyExists() {
        when(userRepository.findByUsernameAndKeycloakId(username, keycloakId))
                .thenReturn(Optional.of(new User()));

        boolean result = userService.createUser(jwt);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenNotExists() {
        when(userRepository.findByUsernameAndKeycloakId(username, keycloakId))
                .thenReturn(Optional.empty());
        when(jwt.getClaim("email")).thenReturn(email); // <-- move here

        boolean result = userService.createUser(jwt);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
