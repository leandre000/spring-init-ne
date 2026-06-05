package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.MeterReadingDto;
import com.utility.billing.dto.MeterReadingRequest;
import com.utility.billing.service.MeterReadingService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/meter-readings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Meter Reading Capture", description = "Endpoints for operators to log utility readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "readingDate", "consumption");

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(summary = "Record a new meter reading (Operator only)")
    public ResponseEntity<ApiResponse<MeterReadingDto>> capture(
            @Valid @RequestBody MeterReadingRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        MeterReadingDto dto = meterReadingService.capture(request, userDetails.getUsername());
        return ResponseBuilder.created(dto, "Reading captured successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get reading details by ID")
    public ResponseEntity<ApiResponse<MeterReadingDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the meter reading", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        MeterReadingDto dto = meterReadingService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "List and search reading logs")
    public ResponseEntity<ApiResponse<PagedResponse<MeterReadingDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by meter ID")
            @RequestParam(required = false) Long meterId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by billing month (1-12)")
            @RequestParam(required = false) Integer month,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by billing year (e.g., 2026)")
            @RequestParam(required = false) Integer year,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, readingDate, consumption)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<MeterReadingDto> result = meterReadingService.findAll(meterId, month, year, pageable);
        return ResponseBuilder.ok(result, "Readings retrieved successfully", httpServletRequest);
    }
}
