package com.example.gontask.repository;

import com.example.gontask.entity.EmployeeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeGroupRepository extends JpaRepository<EmployeeGroup, Long> {
}