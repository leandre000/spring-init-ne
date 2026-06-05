package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.PagedResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.common.pagination.PaginationUtil;
import com.utility.billing.dto.NotificationDto;
import com.utility.billing.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Control", description = "Endpoints for viewing customer notification alerts")
public class NotificationController {

    private final NotificationService notificationService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt", "status");

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get notification details by ID")
    public ResponseEntity<ApiResponse<NotificationDto>> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the notification", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        NotificationDto dto = notificationService.findById(id);
        return ResponseBuilder.ok(dto, httpServletRequest);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all customer notifications (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDto>>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter notifications by customer ID")
            @RequestParam(required = false) Long customerId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed, default: 0)")
            @RequestParam(required = false) Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (default: 10)")
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting field (e.g., id, createdAt, status)")
            @RequestParam(required = false) String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "Sorting direction (asc or desc)")
            @RequestParam(required = false) String sortDir,
            HttpServletRequest httpServletRequest) {
        Pageable pageable = PaginationUtil.toPageable(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<NotificationDto> result = notificationService.findAll(customerId, pageable);
        return ResponseBuilder.ok(result, "Notifications retrieved successfully", httpServletRequest);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get logged-in customer's own notifications")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> findMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        List<NotificationDto> result = notificationService.findMyNotifications(userDetails.getUsername());
        return ResponseBuilder.ok(result, httpServletRequest);
    }

    @PostMapping("/{id}/sent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark a notification as SENT")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsSent(
            @io.swagger.v3.oas.annotations.Parameter(description = "The unique database ID of the notification to mark as sent", required = true)
            @PathVariable Long id,
            HttpServletRequest httpServletRequest) {
        NotificationDto dto = notificationService.markAsSent(id);
        return ResponseBuilder.ok(dto, "Notification marked as sent", httpServletRequest);
    }
}
