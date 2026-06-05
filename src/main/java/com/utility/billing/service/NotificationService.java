package com.utility.billing.service;

import com.utility.billing.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationDto findById(Long id);
    Page<NotificationDto> findAll(Long customerId, Pageable pageable);
    List<NotificationDto> findMyNotifications(String email);
    NotificationDto markAsSent(Long id);
}
