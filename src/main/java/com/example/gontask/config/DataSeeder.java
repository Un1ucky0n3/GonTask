package com.example.gontask.config;

import com.example.gontask.entity.Account;
import com.example.gontask.entity.Employee;
import com.example.gontask.entity.Manager;
import com.example.gontask.entity.EmployeeGroup;
import com.example.gontask.entity.Task;
import com.example.gontask.repository.AccountRepository;
import com.example.gontask.repository.EmployeeRepository;
import com.example.gontask.repository.ManagerRepository;
import com.example.gontask.repository.EmployeeGroupRepository;
import com.example.gontask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final EmployeeGroupRepository employeeGroupRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedEnabled;

    public DataSeeder(
            AccountRepository accountRepository,
            EmployeeRepository employeeRepository,
            ManagerRepository managerRepository,
            EmployeeGroupRepository employeeGroupRepository,
            TaskRepository taskRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.enabled:false}") boolean seedEnabled
    ) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
        this.employeeGroupRepository = employeeGroupRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {

        // Demo data is for local development only (set SEED_DATA=true in .env)
        if (!seedEnabled) {
            return;
        }

        // Prevent creating duplicate data every time the app starts
        if (accountRepository.count() > 0) {
            return;
        }

        // ==========================
        // ACCOUNTS
        // ==========================

        Account managerAccount = new Account(
                "manager@gontask.com",
                passwordEncoder.encode("password123"),
                Account.Role.MANAGER
        );

        Account employeeAccount = new Account(
                "employee@gontask.com",
                passwordEncoder.encode("password123"),
                Account.Role.EMPLOYEE
        );

        accountRepository.save(managerAccount);
        accountRepository.save(employeeAccount);


        // ==========================
        // EMPLOYEE / MANAGER
        // ==========================

        Manager manager = new Manager(
                managerAccount,
                "John",
                "Manager"
        );

        Employee employee = new Employee(
                employeeAccount,
                "Mike",
                "Employee"
        );

        managerRepository.save(manager);
        employeeRepository.save(employee);


        // ==========================
        // GROUP
        // ==========================

        EmployeeGroup group = new EmployeeGroup(
                "Development Team",
                manager
        );

        group.addEmployee(employee);

        employeeGroupRepository.save(group);


        // ==========================
        // TASK
        // ==========================

        Task task = new Task(
                "Build login page",
                "Create the initial login page for GonTask.",
                employee,
                manager,
                LocalDateTime.now().plusDays(7)
        );

        taskRepository.save(task);

        System.out.println("=================================");
        System.out.println("GonTask test data created!");
        System.out.println("Manager: manager@gontask.com");
        System.out.println("Employee: employee@gontask.com");
        System.out.println("=================================");
    }
}