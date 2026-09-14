package com.example.gontask.dto.employee;

public class EmployeeResponse {

    private Long id;
    private Long accountId;
    private String email;
    private String firstName;
    private String lastName;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            Long id,
            Long accountId,
            String email,
            String firstName,
            String lastName
    ) {
        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}