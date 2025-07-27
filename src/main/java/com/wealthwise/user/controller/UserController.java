package com.wealthwise.user.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wealthwise.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        logger.info("Received request to create user with subject (userId): {}", userId);

        boolean created = userService.createUser(jwt);

        if (created) {
            logger.info("New user successfully created: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            logger.info("User already exists: {}", userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
