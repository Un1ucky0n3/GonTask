package com.example.gontask.service;

import com.example.gontask.dto.auth.LoginRequest;
import com.example.gontask.dto.auth.LoginResponse;
import com.example.gontask.dto.auth.MeResponse;
import com.example.gontask.dto.auth.RegisterManagerRequest;
import com.example.gontask.entity.Account;
import com.example.gontask.entity.Employee;
import com.example.gontask.entity.Manager;
import com.example.gontask.exception.BadRequestException;
import com.example.gontask.exception.ForbiddenException;
import com.example.gontask.exception.ResourceNotFoundException;
import com.example.gontask.exception.UnauthorizedException;
import com.example.gontask.repository.AccountRepository;
import com.example.gontask.repository.EmployeeRepository;
import com.example.gontask.repository.ManagerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;

    public AuthService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ManagerRepository managerRepository,
            EmployeeRepository employeeRepository
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.managerRepository = managerRepository;
        this.employeeRepository = employeeRepository;
    }

    public LoginResponse login(LoginRequest request) {

        String email = Account.normalizeEmail(request.getEmail());

        Account account = accountRepository.findByEmail(email)
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

    /**
     * Bootstrap rule: the very first manager may register anonymously.
     * After that, only an already-logged-in manager can create more managers.
     */
    @Transactional
    public void registerManager(
            RegisterManagerRequest request,
            Authentication authentication
    ) {

        if (managerRepository.count() > 0 && !isManager(authentication)) {
            throw new ForbiddenException(
                    "Only an existing manager can register new managers"
            );
        }

        String email = Account.normalizeEmail(request.getEmail());

        if (accountRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Account account = new Account(
                email,
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

    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Account no longer exists")
                );

        if (account.getRole() == Account.Role.MANAGER) {
            Manager manager = managerRepository.findByAccountEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Manager not found")
                    );

            return new MeResponse(
                    account.getId(),
                    account.getEmail(),
                    account.getRole(),
                    manager.getId(),
                    manager.getFirstName(),
                    manager.getLastName()
            );
        }

        Employee employee = employeeRepository.findByAccountEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );

        return new MeResponse(
                account.getId(),
                account.getEmail(),
                account.getRole(),
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName()
        );
    }

    private boolean isManager(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority()
                        .equals("ROLE_MANAGER")
        );
    }
}
