package com.example.gontask.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GroupRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    public GroupRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
