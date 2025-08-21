// src/main/java/com/example/todoapp/repository/TodoRepository.java
package com.example.todoapp.repository;

import com.example.todoapp.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompletedOrderByCreatedAtDesc(boolean completed);
    List<Todo> findAllByOrderByCreatedAtDesc();
    List<Todo> findByTitleContainingIgnoreCase(String title);
}