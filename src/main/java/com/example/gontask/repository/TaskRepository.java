package com.example.gontask.repository;

import com.example.gontask.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByManagerAccountEmailOrderByCreatedAtDesc(String email);

    List<Task> findByManagerAccountEmailAndStatusOrderByCreatedAtDesc(
            String email,
            Task.Status status
    );

    List<Task> findByEmployeeAccountEmailOrderByCreatedAtDesc(String email);

    List<Task> findByEmployeeAccountEmailAndStatusOrderByCreatedAtDesc(
            String email,
            Task.Status status
    );
}
