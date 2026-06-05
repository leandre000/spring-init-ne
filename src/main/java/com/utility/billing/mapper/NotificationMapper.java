package com.utility.billing.mapper;

import com.utility.billing.dto.NotificationDto;
import com.utility.billing.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationDto.builder()
                .id(notification.getId())
                .customerId(notification.getCustomer() != null ? notification.getCustomer().getId() : null)
                .customerName(notification.getCustomer() != null ? notification.getCustomer().getFullName() : null)
                .message(notification.getMessage())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
