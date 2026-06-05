package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.PaymentDto;
import com.utility.billing.dto.PaymentRequest;
import com.utility.billing.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment Processing", description = "Endpoints for posting and viewing customer payments")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "paymentReference", "amountPaid", "paymentDate");

    @PostMapping
    @PreAuthorize("hasRole('FINANCE')")
    @Operation(summary = "Record customer payment (Finance role only)")
    public ResponseEntity<ApiResponse<PaymentDto>> recordPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        PaymentDto dto = paymentService.recordPayment(request, userDetails.getUsername());
        return ResponseBuilder.created(dto, "Payment recorded successfully", httpServletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    @Operation(summary = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the payment", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        PaymentDto dto = paymentService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    @Operation(summary = "Get payment details by payment reference")
    public ResponseEntity<ApiResponse<PaymentDto>> findByReference(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique payment reference string", required = true)
            @PathVariable String reference,
            HttpServletRequest httpServletRequest) {
        PaymentDto dto = paymentService.findByReference(reference);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    @Operation(summary = "List and search payments")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter payments by bill ID")
            @RequestParam(required = false) Long billId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, paymentReference, amountPaid, paymentDate)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<PaymentDto> result = paymentService.findAll(billId, pageable);
        return ResponseBuilder.ok(result, "Payments retrieved successfully", httpServletRequest);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get logged-in customer's own payment history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> findMyPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        List<PaymentDto> result = paymentService.findMyPayments(userDetails.getUsername());
        return ResponseBuilder.ok(result, httpServletRequest);
    }
}
