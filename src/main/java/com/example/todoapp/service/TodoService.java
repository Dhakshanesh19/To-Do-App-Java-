// src/main/java/com/example/todoapp/service/TodoService.java
package com.example.todoapp.service;

import com.example.todoapp.dto.CreateTodoRequest;
import com.example.todoapp.dto.TodoDto;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoDto> getAllTodos() {
        return todoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TodoDto::new)
                .collect(Collectors.toList());
    }

    public List<TodoDto> getTodosByStatus(boolean completed) {
        return todoRepository.findByCompletedOrderByCreatedAtDesc(completed)
                .stream()
                .map(TodoDto::new)
                .collect(Collectors.toList());
    }

    public List<TodoDto> searchTodosByTitle(String title) {
        return todoRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(TodoDto::new)
                .collect(Collectors.toList());
    }

    public Optional<TodoDto> getTodoById(Long id) {
        return todoRepository.findById(id).map(TodoDto::new);
    }

    public TodoDto createTodo(CreateTodoRequest request) {
        Todo todo = new Todo(request.getTitle(), request.getDescription());
        Todo savedTodo = todoRepository.save(todo);
        return new TodoDto(savedTodo);
    }

    public Optional<TodoDto> updateTodo(Long id, CreateTodoRequest request) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(request.getTitle());
                    todo.setDescription(request.getDescription());
                    Todo updatedTodo = todoRepository.save(todo);
                    return new TodoDto(updatedTodo);
                });
    }

    public Optional<TodoDto> toggleTodoCompletion(Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setCompleted(!todo.isCompleted());
                    Todo updatedTodo = todoRepository.save(todo);
                    return new TodoDto(updatedTodo);
                });
    }

    public boolean deleteTodo(Long id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long getTotalCount() {
        return todoRepository.count();
    }

    public long getCompletedCount() {
        return todoRepository.findByCompletedOrderByCreatedAtDesc(true).size();
    }

    public long getPendingCount() {
        return todoRepository.findByCompletedOrderByCreatedAtDesc(false).size();
    }
}