package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TodoAppSimulation extends Simulation {

    val httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling Performance Test")

    val scn = scenario("Todo App Load Test")
            .exec(http("Get Home Page")
                    .get("/")
                    .check(status.is(200)))
            .pause(2)
            .exec(http("Get All Todos")
                    .get("/api/todos")
                    .check(status.is(200)))
            .pause(1)
            .exec(http("Create Todo")
                    .post("/api/todos")
                    .header("Content-Type", "application/json")
                    .body(StringBody("""{"title":"Test Todo","description":"Created by Gatling","completed":false}"""))
                    .check(status.is(201)))
            .pause(1)

    setUp(
            scn.inject(
            atOnceUsers(1),
    rampUsers(10) during (30 seconds),
    constantUsersPerSec(2) during (60 seconds)
            )
            ).protocols(httpProtocol)
    .maxDuration(5 minutes)
    .assertions(
            global.responseTime.max.lt(5000),
      global.responseTime.mean.lt(1000),
              global.successfulRequests.percent.gt(95)
              )
}