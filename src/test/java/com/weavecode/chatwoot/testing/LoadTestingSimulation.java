package com.weavecode.chatwoot.testing;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Load Testing Simulation for Chatwoot Backend
 * Tests the system under various load conditions
 */
public class LoadTestingSimulation extends Simulation {

    // HTTP Protocol Configuration
    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .userAgentHeader("Gatling Load Test")
        .shareConnections();

    // Test Data
    private final FeederBuilder<String> userFeeder = csv("test-data/users.csv").random();
    private final FeederBuilder<String> tenantFeeder = csv("test-data/tenants.csv").random();

    // Scenarios
    private final ScenarioBuilder healthCheckScenario = scenario("Health Check")
        .exec(http("Health Check")
            .get("/actuator/health")
            .check(status().is(200))
            .check(jsonPath("$.status").is("UP")));

    private final ScenarioBuilder authenticationScenario = scenario("Authentication Flow")
        .feed(userFeeder)
        .exec(http("Login Request")
            .post("/api/auth/login")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"email\": \"#{email}\", \"password\": \"#{password}\"}"))
            .check(status().is(200))
            .check(jsonPath("$.token").exists())
            .check(jsonPath("$.token").saveAs("authToken")))
        .exec(http("Get User Profile")
            .get("/api/auth/profile")
            .header("Authorization", "Bearer #{authToken}")
            .check(status().is(200)));

    private final ScenarioBuilder tenantManagementScenario = scenario("Tenant Management")
        .feed(tenantFeeder)
        .exec(http("Create Tenant")
            .post("/api/tenants")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"name\": \"#{name}\", \"domain\": \"#{domain}\", \"plan\": \"#{plan}\"}"))
            .check(status().is(201))
            .check(jsonPath("$.id").exists())
            .check(jsonPath("$.id").saveAs("tenantId")))
        .exec(http("Get Tenant")
            .get("/api/tenants/#{tenantId}")
            .check(status().is(200)));

    private final ScenarioBuilder conversationScenario = scenario("Conversation Operations")
        .feed(userFeeder)
        .exec(http("Create Conversation")
            .post("/api/conversations")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"title\": \"Test Conversation\", \"participantIds\": [\"#{userId}\"]}"))
            .check(status().is(201))
            .check(jsonPath("$.id").exists())
            .check(jsonPath("$.id").saveAs("conversationId")))
        .exec(http("Send Message")
            .post("/api/conversations/#{conversationId}/messages")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"content\": \"Test message\", \"type\": \"TEXT\"}"))
            .check(status().is(201)))
        .exec(http("Get Conversation")
            .get("/api/conversations/#{conversationId}")
            .check(status().is(200)));

    private final ScenarioBuilder userManagementScenario = scenario("User Management")
        .feed(userFeeder)
        .exec(http("Create User")
            .post("/api/users")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"email\": \"#{email}\", \"name\": \"#{name}\", \"role\": \"AGENT\"}"))
            .check(status().is(201))
            .check(jsonPath("$.id").exists())
            .check(jsonPath("$.id").saveAs("newUserId")))
        .exec(http("Update User")
            .put("/api/users/#{newUserId}")
            .header("Content-Type", "application/json")
            .body(StringBody("{\"name\": \"Updated #{name}\"}"))
            .check(status().is(200)))
        .exec(http("Get Users")
            .get("/api/users")
            .queryParam("page", "0")
            .queryParam("size", "10")
            .check(status().is(200)));

    private final ScenarioBuilder metricsScenario = scenario("Metrics Endpoints")
        .exec(http("Get Metrics")
            .get("/actuator/metrics")
            .check(status().is(200)))
        .exec(http("Get Prometheus Metrics")
            .get("/actuator/prometheus")
            .check(status().is(200)))
        .exec(http("Get Health")
            .get("/actuator/health")
            .check(status().is(200)));

    // Load Patterns
    private final LoadBuilder.LoadBuilderStep healthCheckLoad = 
        rampUsers(10).during(30).then(atOnceUsers(50));

    private final LoadBuilder.LoadBuilderStep authenticationLoad = 
        rampUsers(20).during(60).then(rampUsers(100).during(120));

    private final LoadBuilder.LoadBuilderStep tenantLoad = 
        rampUsers(5).during(30).then(rampUsers(20).during(60));

    private final LoadBuilder.LoadBuilderStep conversationLoad = 
        rampUsers(30).during(90).then(rampUsers(150).during(180));

    private final LoadBuilder.LoadBuilderStep userManagementLoad = 
        rampUsers(15).during(45).then(rampUsers(50).during(90));

    private final LoadBuilder.LoadBuilderStep metricsLoad = 
        rampUsers(25).during(60).then(rampUsers(75).during(120));

    // Simulation Setup
    {
        setUp(
            // Health checks - low load, high frequency
            healthCheckScenario.injectOpen(healthCheckLoad),
            
            // Authentication - medium load
            authenticationScenario.injectOpen(authenticationLoad),
            
            // Tenant management - low load
            tenantManagementScenario.injectOpen(tenantLoad),
            
            // Conversation operations - high load
            conversationScenario.injectOpen(conversationLoad),
            
            // User management - medium load
            userManagementScenario.injectOpen(userManagementLoad),
            
            // Metrics endpoints - medium load
            metricsScenario.injectOpen(metricsLoad)
        )
        .protocols(httpProtocol)
        .assertions(
            // Response time assertions
            global().responseTime().mean().lt(500),
            global().responseTime().percentile3().lt(1000),
            
            // Success rate assertions
            global().successfulRequests().percent().gt(95.0),
            
            // Specific endpoint assertions
            forAll().responseTime().mean().lt(800),
            forAll().successfulRequests().percent().gt(90.0)
        );
    }
}
