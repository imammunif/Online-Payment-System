package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.login.LoginRequestDto;
import com.dansmultipro.ops.dto.login.LoginResponseDto;
import com.dansmultipro.ops.exception.InvalidCredentialsException;
import com.dansmultipro.ops.exception.RateLimiterException;
import com.dansmultipro.ops.service.RateLimiterService;
import com.dansmultipro.ops.service.UserService;
import com.dansmultipro.ops.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RateLimiterService rateLimiterService;

    public LoginController(AuthenticationManager authenticationManager, UserService userService, RateLimiterService rateLimiterService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> loginUser(
            @RequestBody @Valid LoginRequestDto data
    ) {
        String email = data.getEmail();
        if (!rateLimiterService.allowRequest(email)) {
            long remainingWaitSeconds = rateLimiterService.getRemainingWaitSeconds(email);
            throw new RateLimiterException("too many attempts, try again in " + remainingWaitSeconds + " minute(s).");
        }

        try {
            var auth = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
            authenticationManager.authenticate(auth);

            var user = userService.findByEmail(data.getEmail());
            var token = JwtUtil.generateToken(
                    user.getId().toString(),
                    user.getUserRole().getCode(),
                    Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
            rateLimiterService.resetBucket(email);

            return new ResponseEntity<>(new LoginResponseDto(
                    user.getName(),
                    user.getUserRole().getCode(),
                    token
            ), HttpStatus.OK);

        } catch (AuthenticationException ex) {
            long remainingAttempts = rateLimiterService.getRemainingAttempts(email) + 1;
            throw new InvalidCredentialsException("failed, wrong credentials. Remaining attempts: " + remainingAttempts);
        }
    }

}
