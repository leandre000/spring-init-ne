package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.TariffDto;
import com.utility.billing.dto.TariffRequest;
import com.utility.billing.service.TariffService;
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
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tariff Configuration", description = "Endpoints for admins to configure pricing rates")
public class TariffController {

    private final TariffService tariffService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "tariffName", "version", "ratePerUnit");

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish a new tariff version")
    public ResponseEntity<ApiResponse<TariffDto>> create(
            @Valid @RequestBody TariffRequest request,
            HttpServletRequest httpServletRequest) {
        TariffDto dto = tariffService.create(request);
        return ResponseBuilder.created(dto, "Tariff version published successfully", httpServletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update generic metadata of a tariff version")
    public ResponseEntity<ApiResponse<TariffDto>> update(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the tariff to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TariffRequest request,
            HttpServletRequest httpServletRequest) {
        TariffDto dto = tariffService.update(id, request);
        return ResponseBuilder.ok(dto, "Tariff updated successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    @Operation(summary = "Get tariff details by ID")
    public ResponseEntity<ApiResponse<TariffDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the tariff", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        TariffDto dto = tariffService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    @Operation(summary = "List and search tariff versions")
    public ResponseEntity<ApiResponse<PagedResponse<TariffDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Search query matching tariff name")
            @RequestParam(required = false) String search,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by tariff status (e.g., ACTIVE, INACTIVE)")
            @RequestParam(required = false) String status,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, tariffName, version, ratePerUnit)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<TariffDto> result = tariffService.findAll(search, status, pageable);
        return ResponseBuilder.ok(result, "Tariffs retrieved successfully", httpServletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete tariff version")
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the tariff to delete", required = true)
            @PathVariable Long id) {
        tariffService.delete(id);
        return ResponseBuilder.noContent();
    }
}
