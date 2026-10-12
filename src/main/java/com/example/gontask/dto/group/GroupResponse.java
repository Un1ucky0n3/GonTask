package com.example.gontask.dto.group;

import com.example.gontask.dto.employee.EmployeeResponse;

import java.util.List;

public class GroupResponse {

    private Long id;
    private String name;
    private Long managerId;
    private List<EmployeeResponse> employees;

    public GroupResponse() {
    }

    public GroupResponse(
            Long id,
            String name,
            Long managerId,
            List<EmployeeResponse> employees
    ) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
        this.employees = employees;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getManagerId() {
        return managerId;
    }

    public List<EmployeeResponse> getEmployees() {
        return employees;
    }
}
