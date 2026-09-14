package com.example.gontask.dto.task;

import com.example.gontask.entity.Task;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.Status status;

    private Long employeeId;
    private String employeeName;

    private Long managerId;
    private String managerName;

    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    public TaskResponse() {
    }

    public TaskResponse(
            Long id,
            String title,
            String description,
            Task.Status status,
            Long employeeId,
            String employeeName,
            Long managerId,
            String managerName,
            LocalDateTime createdAt,
            LocalDateTime dueDate
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.managerId = managerId;
        this.managerName = managerName;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Task.Status getStatus() {
        return status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
}