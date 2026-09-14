package com.example.gontask.dto.auth;

import com.example.gontask.entity.Account;

public class LoginResponse {

    private Long accountId;
    private String email;
    private Account.Role role;

    public LoginResponse() {
    }

    public LoginResponse(
            Long accountId,
            String email,
            Account.Role role
    ) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public Account.Role getRole() {
        return role;
    }
}