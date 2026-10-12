package com.example.gontask.controller;

import com.example.gontask.dto.task.CreateTaskRequest;
import com.example.gontask.dto.task.TaskResponse;
import com.example.gontask.dto.task.UpdateTaskRequest;
import com.example.gontask.dto.task.UpdateTaskStatusRequest;
import com.example.gontask.entity.Task;
import com.example.gontask.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {
        return taskService.createTask(request, authentication);
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) Task.Status status,
            Authentication authentication
    ) {
        return taskService.getTasks(status, authentication);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return taskService.getTaskById(id, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication
    ) {
        return taskService.updateTask(id, request, authentication);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public TaskResponse updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            Authentication authentication
    ) {
        return taskService.updateTaskStatus(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        taskService.deleteTask(id, authentication);
    }
}
