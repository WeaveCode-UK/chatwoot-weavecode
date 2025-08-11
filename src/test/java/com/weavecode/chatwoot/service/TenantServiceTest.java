package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.dto.TenantUsageStats;
import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
import com.weavecode.chatwoot.exception.EntityNotFoundException;
import com.weavecode.chatwoot.exception.ValidationException;
import com.weavecode.chatwoot.repository.TenantRepository;
import com.weavecode.chatwoot.service.impl.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantService Tests")
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant testTenant;
    private UUID testTenantId;

    @BeforeEach
    void setUp() {
        testTenantId = UUID.randomUUID();
        testTenant = new Tenant();
        testTenant.setId(testTenantId);
        testTenant.setName("Test Company");
        testTenant.setDomain("test.com");
        testTenant.setPlanType(PlanType.FREE);
        testTenant.setStatus("ACTIVE");
        testTenant.setCreatedAt(LocalDateTime.now());
        testTenant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create tenant successfully")
    void shouldCreateTenantSuccessfully() {
        // Given
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);

        // When
        Tenant result = tenantService.create(testTenant);

        // Then
        assertNotNull(result);
        assertEquals(testTenant.getName(), result.getName());
        verify(tenantRepository).save(testTenant);
    }

    @Test
    @DisplayName("Should find tenant by ID successfully")
    void shouldFindTenantByIdSuccessfully() {
        // Given
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));

        // When
        Tenant result = tenantService.findById(testTenantId);

        // Then
        assertNotNull(result);
        assertEquals(testTenantId, result.getId());
        verify(tenantRepository).findById(testTenantId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when tenant not found")
    void shouldThrowEntityNotFoundExceptionWhenTenantNotFound() {
        // Given
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> tenantService.findById(testTenantId));
        verify(tenantRepository).findById(testTenantId);
    }

    @Test
    @DisplayName("Should update tenant successfully")
    void shouldUpdateTenantSuccessfully() {
        // Given
        Tenant updatedTenant = new Tenant();
        updatedTenant.setId(testTenantId);
        updatedTenant.setName("Updated Company");
        updatedTenant.setDomain("updated.com");

        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(updatedTenant);

        // When
        Tenant result = tenantService.update(testTenantId, updatedTenant);

        // Then
        assertNotNull(result);
        assertEquals("Updated Company", result.getName());
        verify(tenantRepository).findById(testTenantId);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should delete tenant successfully")
    void shouldDeleteTenantSuccessfully() {
        // Given
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));
        doNothing().when(tenantRepository).deleteById(testTenantId);

        // When
        tenantService.delete(testTenantId);

        // Then
        verify(tenantRepository).findById(testTenantId);
        verify(tenantRepository).deleteById(testTenantId);
    }

    @Test
    @DisplayName("Should find all tenants with pagination")
    void shouldFindAllTenantsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tenant> tenantPage = new PageImpl<>(List.of(testTenant), pageable, 1);
        
        when(tenantRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tenantPage);

        // When
        Page<Tenant> result = tenantService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTenant, result.getContent().get(0));
        verify(tenantRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should find tenant by domain successfully")
    void shouldFindTenantByDomainSuccessfully() {
        // Given
        String domain = "test.com";
        when(tenantRepository.findByDomain(domain)).thenReturn(Optional.of(testTenant));

        // When
        Optional<Tenant> result = tenantService.findByDomain(domain);

        // Then
        assertTrue(result.isPresent());
        assertEquals(domain, result.get().getDomain());
        verify(tenantRepository).findByDomain(domain);
    }

    @Test
    @DisplayName("Should find tenants by plan type successfully")
    void shouldFindTenantsByPlanTypeSuccessfully() {
        // Given
        PlanType planType = PlanType.FREE;
        when(tenantRepository.findByPlanType(planType)).thenReturn(List.of(testTenant));

        // When
        List<Tenant> result = tenantService.findByPlanType(planType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(planType, result.get(0).getPlanType());
        verify(tenantRepository).findByPlanType(planType);
    }

    @Test
    @DisplayName("Should activate subscription successfully")
    void shouldActivateSubscriptionSuccessfully() {
        // Given
        PlanType newPlan = PlanType.PRO;
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);

        // When
        Tenant result = tenantService.activateSubscription(testTenantId, newPlan);

        // Then
        assertNotNull(result);
        verify(tenantRepository).findById(testTenantId);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should suspend tenant successfully")
    void shouldSuspendTenantSuccessfully() {
        // Given
        String reason = "Payment overdue";
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);

        // When
        Tenant result = tenantService.suspendTenant(testTenantId, reason);

        // Then
        assertNotNull(result);
        verify(tenantRepository).findById(testTenantId);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Should get usage stats successfully")
    void shouldGetUsageStatsSuccessfully() {
        // Given
        when(tenantRepository.findById(testTenantId)).thenReturn(Optional.of(testTenant));

        // When
        TenantUsageStats result = tenantService.getUsageStats(testTenantId);

        // Then
        assertNotNull(result);
        assertEquals(testTenantId, result.getTenantId());
        verify(tenantRepository).findById(testTenantId);
    }

    @Test
    @DisplayName("Should validate business rules successfully")
    void shouldValidateBusinessRulesSuccessfully() {
        // Given
        Tenant validTenant = new Tenant();
        validTenant.setName("Valid Company");
        validTenant.setDomain("valid.com");
        validTenant.setPlanType(PlanType.FREE);

        // When & Then
        assertDoesNotThrow(() -> tenantService.validateBusinessRules(validTenant));
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid tenant")
    void shouldThrowValidationExceptionForInvalidTenant() {
        // Given
        Tenant invalidTenant = new Tenant();
        invalidTenant.setName(""); // Invalid: empty name
        invalidTenant.setDomain(""); // Invalid: empty domain

        // When & Then
        assertThrows(ValidationException.class, () -> tenantService.validateBusinessRules(invalidTenant));
    }
}
