package com.example.gontask.repository;

import com.example.gontask.entity.EmployeeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeGroupRepository extends JpaRepository<EmployeeGroup, Long> {

    List<EmployeeGroup> findByManagerAccountEmailOrderByNameAsc(String email);
}
