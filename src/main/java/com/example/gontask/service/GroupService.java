package com.example.gontask.service;

import com.example.gontask.dto.employee.EmployeeResponse;
import com.example.gontask.dto.group.GroupRequest;
import com.example.gontask.dto.group.GroupResponse;
import com.example.gontask.entity.Employee;
import com.example.gontask.entity.EmployeeGroup;
import com.example.gontask.entity.Manager;
import com.example.gontask.exception.ForbiddenException;
import com.example.gontask.exception.ResourceNotFoundException;
import com.example.gontask.repository.EmployeeGroupRepository;
import com.example.gontask.repository.EmployeeRepository;
import com.example.gontask.repository.ManagerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class GroupService {

    private final EmployeeGroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;

    public GroupService(
            EmployeeGroupRepository groupRepository,
            EmployeeRepository employeeRepository,
            ManagerRepository managerRepository
    ) {
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
    }

    public GroupResponse createGroup(
            GroupRequest request,
            Authentication authentication
    ) {

        Manager manager = managerRepository
                .findByAccountEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manager not found")
                );

        EmployeeGroup group = new EmployeeGroup(request.getName(), manager);

        return toResponse(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getMyGroups(Authentication authentication) {
        return groupRepository
                .findByManagerAccountEmailOrderByNameAsc(
                        authentication.getName()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long id, Authentication authentication) {
        return toResponse(findOwnedGroup(id, authentication));
    }

    public GroupResponse renameGroup(
            Long id,
            GroupRequest request,
            Authentication authentication
    ) {
        EmployeeGroup group = findOwnedGroup(id, authentication);
        group.setName(request.getName());
        return toResponse(groupRepository.save(group));
    }

    public void deleteGroup(Long id, Authentication authentication) {
        EmployeeGroup group = findOwnedGroup(id, authentication);
        groupRepository.delete(group);
    }

    public GroupResponse addEmployee(
            Long groupId,
            Long employeeId,
            Authentication authentication
    ) {
        EmployeeGroup group = findOwnedGroup(groupId, authentication);
        Employee employee = findEmployee(employeeId);

        group.addEmployee(employee);

        return toResponse(groupRepository.save(group));
    }

    public GroupResponse removeEmployee(
            Long groupId,
            Long employeeId,
            Authentication authentication
    ) {
        EmployeeGroup group = findOwnedGroup(groupId, authentication);
        Employee employee = findEmployee(employeeId);

        group.removeEmployee(employee);

        return toResponse(groupRepository.save(group));
    }

    // ---------- helpers ----------

    private EmployeeGroup findOwnedGroup(
            Long id,
            Authentication authentication
    ) {
        EmployeeGroup group = groupRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found")
                );

        boolean owner = group.getManager().getAccount().getEmail()
                .equalsIgnoreCase(authentication.getName());

        if (!owner) {
            throw new ForbiddenException(
                    "You do not have access to this group"
            );
        }

        return group;
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );
    }

    private GroupResponse toResponse(EmployeeGroup group) {

        List<EmployeeResponse> employees = group.getEmployees()
                .stream()
                .sorted(Comparator
                        .comparing(Employee::getLastName)
                        .thenComparing(Employee::getFirstName))
                .map(EmployeeResponse::from)
                .toList();

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getManager().getId(),
                employees
        );
    }
}
