package com.weavecode.chatwoot.integration;

import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
import com.weavecode.chatwoot.repository.TenantRepository;
import com.weavecode.chatwoot.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Tenant Integration Tests")
class TenantIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("chatwoot_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private Tenant testTenant;
    private UUID testTenantId;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        tenantRepository.deleteAll();

        // Create test tenant
        testTenantId = UUID.randomUUID();
        testTenant = new Tenant();
        testTenant.setId(testTenantId);
        testTenant.setName("Integration Test Company");
        testTenant.setDomain("integration-test.com");
        testTenant.setPlanType(PlanType.FREE);
        testTenant.setStatus("ACTIVE");
        testTenant.setCreatedAt(LocalDateTime.now());
        testTenant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create and retrieve tenant through repository")
    void shouldCreateAndRetrieveTenantThroughRepository() {
        // Given
        Tenant savedTenant = tenantRepository.save(testTenant);

        // When
        Optional<Tenant> foundTenant = tenantRepository.findById(savedTenant.getId());

        // Then
        assertTrue(foundTenant.isPresent());
        assertEquals("Integration Test Company", foundTenant.get().getName());
        assertEquals("integration-test.com", foundTenant.get().getDomain());
        assertEquals(PlanType.FREE, foundTenant.get().getPlanType());
    }

    @Test
    @DisplayName("Should find tenant by domain through repository")
    void shouldFindTenantByDomainThroughRepository() {
        // Given
        tenantRepository.save(testTenant);

        // When
        Optional<Tenant> foundTenant = tenantRepository.findByDomain("integration-test.com");

        // Then
        assertTrue(foundTenant.isPresent());
        assertEquals(testTenantId, foundTenant.get().getId());
        assertEquals("integration-test.com", foundTenant.get().getDomain());
    }

    @Test
    @DisplayName("Should find tenants by plan type through repository")
    void shouldFindTenantsByPlanTypeThroughRepository() {
        // Given
        Tenant freeTenant = new Tenant();
        freeTenant.setName("Free Company");
        freeTenant.setDomain("free.com");
        freeTenant.setPlanType(PlanType.FREE);
        freeTenant.setStatus("ACTIVE");
        freeTenant.setCreatedAt(LocalDateTime.now());
        freeTenant.setUpdatedAt(LocalDateTime.now());

        Tenant proTenant = new Tenant();
        proTenant.setName("Pro Company");
        proTenant.setDomain("pro.com");
        proTenant.setPlanType(PlanType.PRO);
        proTenant.setStatus("ACTIVE");
        proTenant.setCreatedAt(LocalDateTime.now());
        proTenant.setUpdatedAt(LocalDateTime.now());

        tenantRepository.save(freeTenant);
        tenantRepository.save(proTenant);

        // When
        List<Tenant> freeTenants = tenantRepository.findByPlanType(PlanType.FREE);
        List<Tenant> proTenants = tenantRepository.findByPlanType(PlanType.PRO);

        // Then
        assertEquals(1, freeTenants.size());
        assertEquals(1, proTenants.size());
        assertEquals(PlanType.FREE, freeTenants.get(0).getPlanType());
        assertEquals(PlanType.PRO, proTenants.get(0).getPlanType());
    }

    @Test
    @DisplayName("Should create tenant through service")
    void shouldCreateTenantThroughService() {
        // When
        Tenant createdTenant = tenantService.create(testTenant);

        // Then
        assertNotNull(createdTenant.getId());
        assertEquals("Integration Test Company", createdTenant.getName());
        assertEquals("integration-test.com", createdTenant.getDomain());
        assertEquals(PlanType.FREE, createdTenant.getPlanType());
        assertEquals("ACTIVE", createdTenant.getStatus());
    }

    @Test
    @DisplayName("Should update tenant through service")
    void shouldUpdateTenantThroughService() {
        // Given
        Tenant savedTenant = tenantService.create(testTenant);
        savedTenant.setName("Updated Company Name");
        savedTenant.setPlanType(PlanType.PRO);

        // When
        Tenant updatedTenant = tenantService.update(savedTenant.getId(), savedTenant);

        // Then
        assertEquals("Updated Company Name", updatedTenant.getName());
        assertEquals(PlanType.PRO, updatedTenant.getPlanType());
        assertNotNull(updatedTenant.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete tenant through service")
    void shouldDeleteTenantThroughService() {
        // Given
        Tenant savedTenant = tenantService.create(testTenant);
        UUID tenantId = savedTenant.getId();

        // When
        tenantService.delete(tenantId);

        // Then
        Optional<Tenant> deletedTenant = tenantRepository.findById(tenantId);
        assertFalse(deletedTenant.isPresent());
    }

    @Test
    @DisplayName("Should find tenant by domain through service")
    void shouldFindTenantByDomainThroughService() {
        // Given
        tenantService.create(testTenant);

        // When
        Optional<Tenant> foundTenant = tenantService.findByDomain("integration-test.com");

        // Then
        assertTrue(foundTenant.isPresent());
        assertEquals("integration-test.com", foundTenant.get().getDomain());
    }

    @Test
    @DisplayName("Should find tenants by plan type through service")
    void shouldFindTenantsByPlanTypeThroughService() {
        // Given
        tenantService.create(testTenant);

        // When
        List<Tenant> freeTenants = tenantService.findByPlanType(PlanType.FREE);

        // Then
        assertEquals(1, freeTenants.size());
        assertEquals(PlanType.FREE, freeTenants.get(0).getPlanType());
    }

    @Test
    @DisplayName("Should activate subscription through service")
    void shouldActivateSubscriptionThroughService() {
        // Given
        Tenant savedTenant = tenantService.create(testTenant);

        // When
        Tenant activatedTenant = tenantService.activateSubscription(savedTenant.getId(), PlanType.PRO);

        // Then
        assertEquals(PlanType.PRO, activatedTenant.getPlanType());
        assertEquals("ACTIVE", activatedTenant.getStatus());
    }

    @Test
    @DisplayName("Should suspend tenant through service")
    void shouldSuspendTenantThroughService() {
        // Given
        Tenant savedTenant = tenantService.create(testTenant);
        String suspensionReason = "Payment overdue";

        // When
        Tenant suspendedTenant = tenantService.suspendTenant(savedTenant.getId(), suspensionReason);

        // Then
        assertEquals("SUSPENDED", suspendedTenant.getStatus());
        assertNotNull(suspendedTenant.getSuspendedAt());
    }

    @Test
    @DisplayName("Should get usage stats through service")
    void shouldGetUsageStatsThroughService() {
        // Given
        Tenant savedTenant = tenantService.create(testTenant);

        // When
        var usageStats = tenantService.getUsageStats(savedTenant.getId());

        // Then
        assertNotNull(usageStats);
        assertEquals(savedTenant.getId(), usageStats.getTenantId());
        assertEquals(savedTenant.getName(), usageStats.getTenantName());
    }

    @Test
    @DisplayName("Should handle tenant not found error")
    void shouldHandleTenantNotFoundError() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        assertThrows(Exception.class, () -> tenantService.findById(nonExistentId));
    }

    @Test
    @DisplayName("Should validate business rules")
    void shouldValidateBusinessRules() {
        // Given
        Tenant invalidTenant = new Tenant();
        invalidTenant.setName(""); // Invalid: empty name
        invalidTenant.setDomain(""); // Invalid: empty domain

        // When & Then
        assertThrows(Exception.class, () -> tenantService.validateBusinessRules(invalidTenant));
    }
}
