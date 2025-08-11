package com.weavecode.chatwoot.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class TenantPerformanceTest extends Simulation {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String API_BASE = "/api";

    // HTTP Protocol Configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/Performance Test");

    // Feeder for test data
    private FeederBuilder<String> tenantFeeder = csv("test-data/tenants.csv").random();

    // Headers
    private HttpHeaderBuilder jsonHeader = header("Content-Type", "application/json");
    private HttpHeaderBuilder authHeader = header("Authorization", "Bearer ${authToken}");

    // Scenarios
    private ScenarioBuilder createTenantScenario = scenario("Create Tenant")
            .feed(tenantFeeder)
            .exec(http("Create Tenant Request")
                    .post(API_BASE + "/tenants")
                    .headers(jsonHeader)
                    .body(StringBody("""
                            {
                                "name": "${tenantName}",
                                "domain": "${domain}",
                                "planType": "${planType}",
                                "status": "ACTIVE"
                            }
                            """))
                    .check(status().is(201))
                    .check(jsonPath("$.id").saveAs("tenantId"))
                    .check(jsonPath("$.name").is("${tenantName}")));

    private ScenarioBuilder getTenantScenario = scenario("Get Tenant")
            .exec(http("Get Tenant Request")
                    .get(API_BASE + "/tenants/${tenantId}")
                    .headers(jsonHeader)
                    .check(status().is(200))
                    .check(jsonPath("$.id").is("${tenantId}")));

    private ScenarioBuilder updateTenantScenario = scenario("Update Tenant")
            .exec(http("Update Tenant Request")
                    .put(API_BASE + "/tenants/${tenantId}")
                    .headers(jsonHeader)
                    .body(StringBody("""
                            {
                                "name": "Updated ${tenantName}",
                                "domain": "${domain}",
                                "planType": "PRO",
                                "status": "ACTIVE"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.name").is("Updated ${tenantName}")));

    private ScenarioBuilder listTenantsScenario = scenario("List Tenants")
            .exec(http("List Tenants Request")
                    .get(API_BASE + "/tenants?page=0&size=20")
                    .headers(jsonHeader)
                    .check(status().is(200))
                    .check(jsonPath("$.content").exists()));

    private ScenarioBuilder deleteTenantScenario = scenario("Delete Tenant")
            .exec(http("Delete Tenant Request")
                    .delete(API_BASE + "/tenants/${tenantId}")
                    .headers(jsonHeader)
                    .check(status().is(204)));

    // Load Simulation
    {
        setUp(
                // Create tenants with 10 users for 30 seconds
                createTenantScenario.injectOpen(
                        rampUsers(10).during(30)
                ),
                
                // Get tenants with 20 users for 60 seconds
                getTenantScenario.injectOpen(
                        rampUsers(20).during(60)
                ),
                
                // Update tenants with 15 users for 45 seconds
                updateTenantScenario.injectOpen(
                        rampUsers(15).during(45)
                ),
                
                // List tenants with 25 users for 90 seconds
                listTenantsScenario.injectOpen(
                        rampUsers(25).during(90)
                ),
                
                // Delete tenants with 5 users for 20 seconds
                deleteTenantScenario.injectOpen(
                        rampUsers(5).during(20)
                )
        )
        .protocols(httpProtocol)
        .assertions(
                // Response time assertions
                global().responseTime().mean().lt(500), // Mean response time < 500ms
                global().responseTime().percentile3().lt(1000), // 95% response time < 1000ms
                
                // Throughput assertions
                global().requestsPerSec().gt(50), // More than 50 requests per second
                
                // Error rate assertions
                global().failedRequests().percent().lt(5.0) // Less than 5% failed requests
        );
    }

    // Helper method to create test data CSV
    private void createTestDataCsv() {
        // This would typically be done in a separate setup method
        // or using a data generation tool
    }
}
