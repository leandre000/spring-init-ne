package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.MeterDto;
import com.utility.billing.dto.MeterRequest;
import com.utility.billing.service.MeterService;
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
@RequestMapping("/api/v1/meters")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Meter Management", description = "Endpoints for administering customer meters")
public class MeterController {

    private final MeterService meterService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "meterNumber", "installationDate");

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Install and register a new meter")
    public ResponseEntity<ApiResponse<MeterDto>> create(
            @Valid @RequestBody MeterRequest request,
            HttpServletRequest httpServletRequest) {
        MeterDto dto = meterService.create(request);
        return ResponseBuilder.created(dto, "Meter registered successfully", httpServletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update meter details")
    public ResponseEntity<ApiResponse<MeterDto>> update(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the meter to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MeterRequest request,
            HttpServletRequest httpServletRequest) {
        MeterDto dto = meterService.update(id, request);
        return ResponseBuilder.ok(dto, "Meter updated successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get meter details by ID")
    public ResponseEntity<ApiResponse<MeterDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the meter", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        MeterDto dto = meterService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping("/number/{number}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get meter details by meter number")
    public ResponseEntity<ApiResponse<MeterDto>> findByNumber(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique meter number code", required = true)
            @PathVariable String number,
            HttpServletRequest httpServletRequest) {
        MeterDto dto = meterService.findByNumber(number);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Search and list meters")
    public ResponseEntity<ApiResponse<PagedResponse<MeterDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Search query matching meter number")
            @RequestParam(required = false) String search,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by meter status (e.g., ACTIVE, INACTIVE)")
            @RequestParam(required = false) String status,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, meterNumber, installationDate)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<MeterDto> result = meterService.findAll(search, status, pageable);
        return ResponseBuilder.ok(result, "Meters retrieved successfully", httpServletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete meter")
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the meter to delete", required = true)
            @PathVariable Long id) {
        meterService.delete(id);
        return ResponseBuilder.noContent();
    }
}
