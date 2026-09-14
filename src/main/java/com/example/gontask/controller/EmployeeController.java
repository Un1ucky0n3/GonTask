package com.example.gontask.controller;

import com.example.gontask.dto.employee.CreateEmployeeRequest;
import com.example.gontask.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.gontask.dto.employee.EmployeeResponse;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public void createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request
    ) {
        employeeService.createEmployee(request);
    }
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}