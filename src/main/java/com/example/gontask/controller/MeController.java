package com.example.gontask.controller;

import com.example.gontask.dto.auth.MeResponse;
import com.example.gontask.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final AuthService authService;

    public MeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public MeResponse me(Authentication authentication) {
        return authService.getCurrentUser(authentication);
    }
}
