package utils;

import com.example.todoapp.dto.CreateTodoRequest;
import com.github.javafaker.Faker;

public class FakeDataGenerator {

    static Faker faker = new Faker();

    public static String randomTodoJson(){
        return "{ \"title\": \"" + faker.book().title() + "\", " +
                "\"description\": \"" + faker.lorem().sentence() + "\" }";
    }

//    public static String randomTodoRequest(){
//        return new CreateTodoRequest(faker.book().title(), faker.lorem().sentence()).toString();
//    }
}
