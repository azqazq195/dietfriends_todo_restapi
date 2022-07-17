package com.example.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User getById(int id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
