package com.example.gontask.dto.auth;

import com.example.gontask.entity.Account;

public class LoginResponse {

    private Long accountId;
    private String email;
    private Account.Role role;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(
            Long accountId,
            String email,
            Account.Role role,
            String token
    ) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
        this.token = token;
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

    public String getToken() {
        return token;
    }
}