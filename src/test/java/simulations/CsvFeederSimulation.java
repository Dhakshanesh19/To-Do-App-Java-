package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static utils.PerfTestUtils.createTodoBody;
import static utils.constants.Constants.APPLICATION_JSON;
import static utils.constants.Constants.BASE_URL;

public class CsvFeederSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http.baseUrl(BASE_URL)
            .acceptHeader(APPLICATION_JSON)
            .contentTypeHeader(APPLICATION_JSON);


    FeederBuilder<String> feeder = csv("../resources/data/todos.csv").circular();

    ChainBuilder createTodo = feed(feeder).exec(
            http("Create Todo from Feeder")
                    .post("/api/todos")
                    .body(StringBody(session -> createTodoBody(session.getString("title"), session.getString("completed"))
                    ))
                    .check(status().is(201))
    );

    ScenarioBuilder scn = scenario("CSV Feeder Example")
            .exec(createTodo);
    {
        setUp(
                scn.injectOpen(rampUsers(100).during(10))
        ).protocols(httpProtocol);
    }


}
