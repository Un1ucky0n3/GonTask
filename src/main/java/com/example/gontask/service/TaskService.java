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
            Long managerId
    ) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manager not found")
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
        task.setStatus(request.getStatus());
        task.setEmployee(employee);
        task.setDueDate(request.getDueDate());

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
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {

        Employee employee = task.getEmployee();
        Manager manager = task.getManager();

        String employeeName =
                employee.getFirstName() + " " + employee.getLastName();

        String managerName =
                manager.getFirstName() + " " + manager.getLastName();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                employee.getId(),
                employeeName,
                manager.getId(),
                managerName,
                task.getCreatedAt(),
                task.getDueDate()
        );
    }
}