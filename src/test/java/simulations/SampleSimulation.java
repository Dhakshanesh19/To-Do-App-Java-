package simulations;


import com.example.todoapp.dto.CreateTodoRequest;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static utils.FakeDataGenerator.randomTodoJson;
import static utils.constants.Constants.APPLICATION_JSON;
import static utils.constants.Constants.BASE_URL;

public class SampleSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl(BASE_URL)
            .acceptHeader(APPLICATION_JSON)
            .contentTypeHeader(APPLICATION_JSON);

    ChainBuilder getTodo = exec(
            http("Get all todos")
                    .get("/api/todos")
                    .check(status().is(200)));

    ChainBuilder createTodo = exec(session -> {
        String body = randomTodoJson();
        return session.set("todoBody", body);
    }).exec(
            http("Create Todo")
                    .post("/api/todos")
                    .body(StringBody("#{todoBody}"))
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("todoId")) // save id
    );


    ScenarioBuilder builder = scenario("Sample Simulation")
            .exec(getTodo);

    ScenarioBuilder builder2 = scenario("Create Todo1").exec(createTodo);

// instant spike
//    {
//        setUp(
//                builder.injectOpen(atOnceUsers(10)),
//                builder2.injectOpen(atOnceUsers(10))
//        ).protocols(httpProtocol).assertions(
//                global().responseTime().max().lt(2000),
//                global().successfulRequests().percent().gt(95.0),
//                details("Get all todos").responseTime().mean().lt(1000),
//                details("Create Todo").responseTime().mean().lt(1000)
//        );
//    }

    // gradual ramp

    {
        setUp(
                builder.injectOpen(
                        rampUsers(10).during(20)),
                builder2.injectOpen(rampUsers(10).during(20))
        ).protocols(httpProtocol)
                .maxDuration(10)
                .assertions(
                global().responseTime().max().lt(2000),
                global().successfulRequests().percent().gt(95.0),
                details("Get all todos").responseTime().mean().lt(1000),
                details("Create Todo").responseTime().mean().lt(1000)
        );
    }

//    {
//        setUp(
//                builder.injectOpen(
//                        rampUsers(1000).during(20)
//                ).andThen(builder2.injectOpen(rampUsers(1000).during(20)))
////                ,
////                builder2.injectOpen(rampUsers(1000).during(20))
//        ).protocols(httpProtocol)
//                .maxDuration(60)
//                .assertions(
//                global().responseTime().max().lt(2000),
//                global().successfulRequests().percent().gt(95.0),
//                details("Get all todos").responseTime().mean().lt(1000),
//                details("Create Todo").responseTime().mean().lt(1000)
//        );
//    }

//     ramping rate (smooth load increase)
//    {
//        setUp(
//                builder.injectOpen(
//                        rampUsersPerSec(10).to(200).during(30) // ramp from 10 req/sec → 200 req/sec over 30s
//                ),
//                builder2.injectOpen(
//                        rampUsersPerSec(10).to(200).during(30)
//                )
//        ).protocols(httpProtocol).assertions(
//                global().responseTime().max().lt(2000),
//                global().successfulRequests().percent().gt(95.0)
//        );
//    }

    // steady load (sustained traffic)
//    {
//        setUp(
//                builder.injectOpen(
//                        constantUsersPerSec(50).during(60) // 50 req/sec for 60s
//                ),
//                builder2.injectOpen(
//                        constantUsersPerSec(20).during(60)
//                )
//        ).protocols(httpProtocol).assertions(
//                global().responseTime().mean().lt(1000),
//                global().successfulRequests().percent().gt(95.0)
//        );
//    }

    // stress profile (burst + ramp-down)
//    {
//        setUp(
//                builder.injectOpen(
//                        stressPeakUsers(10000).during(10) // up to 10k concurrent users over 60s
//                ),
//                builder2.injectOpen(
//                        stressPeakUsers(5000).during(10)
//                )
//        ).protocols(httpProtocol).assertions(
//                global().responseTime().percentile3().lt(1500),
//                global().successfulRequests().percent().gt(90.0)
//        );
//    }

}
