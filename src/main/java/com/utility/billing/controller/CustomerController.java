package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.CustomerDto;
import com.utility.billing.dto.CustomerRequest;
import com.utility.billing.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer Management", description = "Endpoints for administering utility customers")
public class CustomerController {

    private final CustomerService customerService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "customerCode", "fullName", "createdAt");

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
    @Operation(summary = "Create a new customer profile")
    public ResponseEntity<ApiResponse<CustomerDto>> create(
            @Valid @RequestBody CustomerRequest request,
            HttpServletRequest httpServletRequest) {
        CustomerDto dto = customerService.create(request);
        return ResponseBuilder.created(dto, "Customer created successfully", httpServletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update an existing customer profile")
    public ResponseEntity<ApiResponse<CustomerDto>> update(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the customer profile to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request,
            HttpServletRequest httpServletRequest) {
        CustomerDto dto = customerService.update(id, request);
        return ResponseBuilder.ok(dto, "Customer updated successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
    @Operation(summary = "Get customer profile by ID")
    public ResponseEntity<ApiResponse<CustomerDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the customer profile", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        CustomerDto dto = customerService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
    @Operation(summary = "Get customer profile by customer code")
    public ResponseEntity<ApiResponse<CustomerDto>> findByCode(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique customer code (e.g. CUST-001)", required = true)
            @PathVariable String code,
            HttpServletRequest httpServletRequest) {
        CustomerDto dto = customerService.findByCode(code);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    @Operation(summary = "Search and list customers (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Search query matching customer name or national ID")
            @RequestParam(required = false) String search,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by customer status (e.g., ACTIVE, INACTIVE)")
            @RequestParam(required = false) String status,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, customerCode, fullName, createdAt)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<CustomerDto> result = customerService.findAll(search, status, pageable);
        return ResponseBuilder.ok(result, "Customers retrieved successfully", httpServletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete customer profile")
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the customer profile to delete", required = true)
            @PathVariable Long id) {
        customerService.delete(id);
        return ResponseBuilder.noContent();
    }
}
