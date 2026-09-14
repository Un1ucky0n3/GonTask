package com.example.gontask.service;

import com.example.gontask.dto.auth.LoginRequest;
import com.example.gontask.dto.auth.LoginResponse;
import com.example.gontask.entity.Account;
import com.example.gontask.exception.UnauthorizedException;
import com.example.gontask.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                account.getPassword()
        )) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(account);

        return new LoginResponse(
                account.getId(),
                account.getEmail(),
                account.getRole(),
                token
        );
    }
}