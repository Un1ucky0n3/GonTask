package com.example.gontask.service;

import com.example.gontask.dto.task.CreateTaskRequest;
import com.example.gontask.dto.task.TaskResponse;
import com.example.gontask.dto.task.UpdateTaskRequest;
import com.example.gontask.dto.task.UpdateTaskStatusRequest;
import com.example.gontask.entity.Employee;
import com.example.gontask.entity.Manager;
import com.example.gontask.entity.Task;
import com.example.gontask.exception.ResourceNotFoundException;
import com.example.gontask.repository.EmployeeRepository;
import com.example.gontask.repository.ManagerRepository;
import com.example.gontask.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;

    public TaskService(
            TaskRepository taskRepository,
            EmployeeRepository employeeRepository,
            ManagerRepository managerRepository
    ) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
    }

    public TaskResponse createTask(
            CreateTaskRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Manager manager = managerRepository.findByAccountEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manager not found")
                );

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                employee,
                manager,
                request.getDueDate()
        );

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        return toResponse(task);
    }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request
    ) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEmployee(employee);
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);

        return toResponse(updatedTask);
    }

    public TaskResponse updateTaskStatus(
            Long id,
            UpdateTaskStatusRequest request
    ) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        task.setStatus(request.getStatus());

        Task updatedTask = taskRepository.save(task);

        return toResponse(updatedTask);
    }

    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {

        String employeeName =
                task.getEmployee().getFirstName() + " "
                        + task.getEmployee().getLastName();

        String managerName =
                task.getManager().getFirstName() + " "
                        + task.getManager().getLastName();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getEmployee().getId(),
                employeeName,
                task.getManager().getId(),
                managerName,
                task.getCreatedAt(),
                task.getDueDate()
        );
    }
}