// src/main/java/com/example/todoapp/controller/TodoController.java
package com.example.todoapp.controller;

import com.example.todoapp.dto.CreateTodoRequest;
import com.example.todoapp.dto.TodoDto;
import com.example.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.javafaker.Faker;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;
    String filePath = "src/test/resources/data/todos.csv";
    Faker faker = new Faker();

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search) {

        List<TodoDto> todos;

        if (search != null && !search.trim().isEmpty()) {
            todos = todoService.searchTodosByTitle(search.trim());
        } else if (completed != null) {
            todos = todoService.getTodosByStatus(completed);
        } else {
            todos = todoService.getAllTodos();
        }

        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id)
                .map(todo -> ResponseEntity.ok(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        TodoDto createdTodo = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id,
                                              @Valid @RequestBody CreateTodoRequest request) {
        return todoService.updateTodo(id, request)
                .map(todo -> ResponseEntity.ok(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDto> toggleTodoCompletion(@PathVariable Long id) {
        return todoService.toggleTodoCompletion(id)
                .map(todo -> ResponseEntity.ok(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        if (todoService.deleteTodo(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTodoStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", todoService.getTotalCount());
        stats.put("completed", todoService.getCompletedCount());
        stats.put("pending", todoService.getPendingCount());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/unstable")
    public ResponseEntity<String> unstableEndpoint() throws InterruptedException {
        if (Math.random() < 0.3) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Random failure");
        }
        else if(Math.random()>0.4 && Math.random()<0.6){
            Thread.sleep(5000);
            return ResponseEntity.ok("This was slow!");
        }
        return ResponseEntity.ok("Success!");
    }


    @GetMapping("/generate-csv")
    public String generateCsv() {
        String filePath = "src/test/resources/data/todos.csv"; // Gatling reads from here

        try (FileWriter writer = new FileWriter(filePath, false)) {
            // Header
            writer.write("title,description\n");

            // Generate 20 fake records
            for (int i = 1; i <= 100; i++) {
                String title = faker.lorem().sentence(3);
                String desc = faker.lorem().sentence(6);
                writer.write(title + "," + desc + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }

        return "CSV generated at " + filePath;
    }
}