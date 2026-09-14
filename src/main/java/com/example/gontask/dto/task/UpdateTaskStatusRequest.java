package com.example.gontask.dto.task;

import com.example.gontask.entity.Task;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private Task.Status status;

    public UpdateTaskStatusRequest() {
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }
}