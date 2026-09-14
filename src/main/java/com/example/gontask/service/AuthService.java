package com.example.gontask.service;

import com.example.gontask.dto.auth.LoginRequest;
import com.example.gontask.dto.auth.LoginResponse;
import com.example.gontask.entity.Account;
import com.example.gontask.exception.UnauthorizedException;
import com.example.gontask.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.gontask.dto.auth.RegisterManagerRequest;
import com.example.gontask.entity.Manager;
import com.example.gontask.exception.BadRequestException;
import com.example.gontask.repository.ManagerRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ManagerRepository managerRepository;

    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ManagerRepository managerRepository
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.managerRepository = managerRepository;
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

    @Transactional
    public void registerManager(RegisterManagerRequest request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Account account = new Account(
                request.getEmail(),
                encodedPassword,
                Account.Role.MANAGER
        );

        Account savedAccount = accountRepository.save(account);

        Manager manager = new Manager(
                savedAccount,
                request.getFirstName(),
                request.getLastName()
        );

        managerRepository.save(manager);
    }
}