package com.example.gontask.service;

import com.example.gontask.dto.task.CreateTaskRequest;
import com.example.gontask.dto.task.TaskResponse;
import com.example.gontask.dto.task.UpdateTaskRequest;
import com.example.gontask.dto.task.UpdateTaskStatusRequest;
import com.example.gontask.entity.Employee;
import com.example.gontask.entity.Manager;
import com.example.gontask.entity.Task;
import com.example.gontask.exception.ForbiddenException;
import com.example.gontask.exception.ResourceNotFoundException;
import com.example.gontask.repository.EmployeeRepository;
import com.example.gontask.repository.ManagerRepository;
import com.example.gontask.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

        Employee employee = findEmployee(request.getEmployeeId());

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                employee,
                manager,
                request.getDueDate()
        );

        return toResponse(taskRepository.save(task));
    }

    /**
     * Managers see the tasks they created; employees see tasks assigned to them.
     * Optional status filter.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(
            Task.Status status,
            Authentication authentication
    ) {

        String email = authentication.getName();
        boolean manager = hasRole(authentication, "MANAGER");

        List<Task> tasks;

        if (manager) {
            tasks = (status == null)
                    ? taskRepository
                    .findByManagerAccountEmailOrderByCreatedAtDesc(email)
                    : taskRepository
                    .findByManagerAccountEmailAndStatusOrderByCreatedAtDesc(
                            email, status
                    );
        } else {
            tasks = (status == null)
                    ? taskRepository
                    .findByEmployeeAccountEmailOrderByCreatedAtDesc(email)
                    : taskRepository
                    .findByEmployeeAccountEmailAndStatusOrderByCreatedAtDesc(
                            email, status
                    );
        }

        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, Authentication authentication) {

        Task task = findTask(id);

        if (!isTaskManager(task, authentication)
                && !isTaskAssignee(task, authentication)) {
            throw new ForbiddenException(
                    "You do not have access to this task"
            );
        }

        return toResponse(task);
    }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request,
            Authentication authentication
    ) {

        Task task = findTask(id);

        if (!isTaskManager(task, authentication)) {
            throw new ForbiddenException(
                    "Only the manager who created this task can edit it"
            );
        }

        Employee employee = findEmployee(request.getEmployeeId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEmployee(employee);
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTaskStatus(
            Long id,
            UpdateTaskStatusRequest request,
            Authentication authentication
    ) {

        Task task = findTask(id);

        if (!isTaskAssignee(task, authentication)) {
            throw new ForbiddenException(
                    "Only the assigned employee can change this task's status"
            );
        }

        task.setStatus(request.getStatus());

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id, Authentication authentication) {

        Task task = findTask(id);

        if (!isTaskManager(task, authentication)) {
            throw new ForbiddenException(
                    "Only the manager who created this task can delete it"
            );
        }

        taskRepository.delete(task);
    }

    // ---------- helpers ----------

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );
    }

    private boolean isTaskManager(Task task, Authentication authentication) {
        return task.getManager().getAccount().getEmail()
                .equalsIgnoreCase(authentication.getName());
    }

    private boolean isTaskAssignee(Task task, Authentication authentication) {
        return task.getEmployee().getAccount().getEmail()
                .equalsIgnoreCase(authentication.getName());
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority().equals("ROLE_" + role)
        );
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
