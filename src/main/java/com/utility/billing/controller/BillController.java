package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.BillDto;
import com.utility.billing.dto.BillRequest;
import com.utility.billing.service.BillingService;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Billing Operations", description = "Endpoints for generating, listing, and approving customer bills")
public class BillController {

    private final BillingService billingService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "billNumber", "totalAmount", "balance", "generatedDate");

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate a utility bill for a meter reading")
    public ResponseEntity<ApiResponse<BillDto>> generateBill(
            @Valid @RequestBody BillRequest request,
            HttpServletRequest httpServletRequest) {
        BillDto dto = billingService.generateBill(request);
        return ResponseBuilder.created(dto, "Bill generated successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    @Operation(summary = "Get bill details by ID")
    public ResponseEntity<ApiResponse<BillDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the bill", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        BillDto dto = billingService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping("/number/{billNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    @Operation(summary = "Get bill details by bill number")
    public ResponseEntity<ApiResponse<BillDto>> findByNumber(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique bill number", required = true)
            @PathVariable String billNumber,
            HttpServletRequest httpServletRequest) {
        BillDto dto = billingService.findByNumber(billNumber);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    @Operation(summary = "List and search all customer bills")
    public ResponseEntity<ApiResponse<PagedResponse<BillDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter bills by customer ID")
            @RequestParam(required = false) Long customerId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter bills by status (e.g., PENDING, PAID, OVERDUE, PARTIAL)")
            @RequestParam(required = false) String status,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, totalAmount, balance, generatedDate)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<BillDto> result = billingService.findAll(customerId, status, pageable);
        return ResponseBuilder.ok(result, "Bills retrieved successfully", httpServletRequest);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get logged-in customer's own bills")
    public ResponseEntity<ApiResponse<List<BillDto>>> findMyBills(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        List<BillDto> result = billingService.findMyBills(userDetails.getUsername());
        return ResponseBuilder.ok(result, httpServletRequest);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('FINANCE')")
    @Operation(summary = "Approve utility bill (Finance role only)")
    public ResponseEntity<ApiResponse<BillDto>> approveBill(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the bill to approve", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        BillDto dto = billingService.approveBill(id);
        return ResponseBuilder.ok(dto, "Bill approved successfully", httpServletRequest);
    }
}
