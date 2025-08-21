// src/main/java/com/example/todoapp/config/DataInitializer.java
package com.example.todoapp.config;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    @Autowired
    public DataInitializer(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Add some sample data
        if (todoRepository.count() == 0) {
            todoRepository.save(new Todo("Learn Spring Boot", "Complete the Spring Boot tutorial"));
            todoRepository.save(new Todo("Build Todo App", "Create a fully functional todo application"));

            Todo completedTodo = new Todo("Setup Development Environment", "Install Java, Maven, and IDE");
            completedTodo.setCompleted(true);
            todoRepository.save(completedTodo);
        }
    }
}