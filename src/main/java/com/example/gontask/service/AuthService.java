package com.example.gontask.service;

import com.example.gontask.dto.auth.LoginRequest;
import com.example.gontask.dto.auth.LoginResponse;
import com.example.gontask.entity.Account;
import com.example.gontask.exception.UnauthorizedException;
import com.example.gontask.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountRepository accountRepository;

    public AuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password")
                );

        if (!account.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return new LoginResponse(
                account.getId(),
                account.getEmail(),
                account.getRole()
        );
    }
}