package utils;

import com.example.todoapp.dto.CreateTodoRequest;

public class PerfTestUtils {

    public static String createTodoBody(String title, String description) {
        return new CreateTodoRequest(title, description).toString();
    }
}
