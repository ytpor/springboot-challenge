package com.ytpor.api.controller;

import com.ytpor.api.model.auth.AuthRequest;
import com.ytpor.api.model.auth.AuthResponse;
import com.ytpor.api.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
@SecurityRequirements()
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    @Operation(summary = "Authentication", description = "Authenticate with the application")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }
}
