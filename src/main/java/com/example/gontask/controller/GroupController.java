package com.example.gontask.controller;

import com.example.gontask.dto.group.GroupRequest;
import com.example.gontask.dto.group.GroupResponse;
import com.example.gontask.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('MANAGER')")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public GroupResponse createGroup(
            @Valid @RequestBody GroupRequest request,
            Authentication authentication
    ) {
        return groupService.createGroup(request, authentication);
    }

    @GetMapping
    public List<GroupResponse> getMyGroups(Authentication authentication) {
        return groupService.getMyGroups(authentication);
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return groupService.getGroup(id, authentication);
    }

    @PutMapping("/{id}")
    public GroupResponse renameGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupRequest request,
            Authentication authentication
    ) {
        return groupService.renameGroup(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(
            @PathVariable Long id,
            Authentication authentication
    ) {
        groupService.deleteGroup(id, authentication);
    }

    @PostMapping("/{id}/employees/{employeeId}")
    public GroupResponse addEmployee(
            @PathVariable Long id,
            @PathVariable Long employeeId,
            Authentication authentication
    ) {
        return groupService.addEmployee(id, employeeId, authentication);
    }

    @DeleteMapping("/{id}/employees/{employeeId}")
    public GroupResponse removeEmployee(
            @PathVariable Long id,
            @PathVariable Long employeeId,
            Authentication authentication
    ) {
        return groupService.removeEmployee(id, employeeId, authentication);
    }
}
