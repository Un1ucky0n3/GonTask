package com.example.gontask.service;

import com.example.gontask.dto.employee.CreateEmployeeRequest;
import com.example.gontask.dto.employee.EmployeeResponse;
import com.example.gontask.entity.Account;
import com.example.gontask.entity.Employee;
import com.example.gontask.exception.BadRequestException;
import com.example.gontask.repository.AccountRepository;
import com.example.gontask.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            AccountRepository accountRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional
    public void createEmployee(CreateEmployeeRequest request) {

        String email = Account.normalizeEmail(request.getEmail());

        if (accountRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Account account = new Account(
                email,
                encodedPassword,
                Account.Role.EMPLOYEE
        );

        Account savedAccount = accountRepository.save(account);

        Employee employee = new Employee(
                savedAccount,
                request.getFirstName(),
                request.getLastName()
        );

        employeeRepository.save(employee);
    }
}
