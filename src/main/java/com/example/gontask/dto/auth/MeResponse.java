package com.example.gontask.dto.auth;

import com.example.gontask.entity.Account;

public class MeResponse {

    private Long accountId;
    private String email;
    private Account.Role role;
    private Long profileId;
    private String firstName;
    private String lastName;

    public MeResponse() {
    }

    public MeResponse(
            Long accountId,
            String email,
            Account.Role role,
            Long profileId,
            String firstName,
            String lastName
    ) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public Long getProfileId() {
        return profileId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
