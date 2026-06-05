package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.NotificationDto;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.Notification;
import com.utility.billing.enums.NotificationStatus;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.NotificationMapper;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.repository.NotificationRepository;
import com.utility.billing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationDto findById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        return notificationMapper.toDto(notification);
    }

    @Override
    public Page<NotificationDto> findAll(Long customerId, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();
        if (customerId != null) {
            criteria.add(new SearchCriteria("customer.id", SearchCriteria.Op.EQUAL, customerId));
        }
        BaseSpecification<Notification> spec = new BaseSpecification<>(criteria);
        Page<Notification> page = notificationRepository.findAll(spec, pageable);
        return page.map(notificationMapper::toDto);
    }

    @Override
    public List<NotificationDto> findMyNotifications(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found for email: " + email));
        List<Notification> notifications = notificationRepository.findByCustomerId(customer.getId());
        return notifications.stream().map(notificationMapper::toDto).toList();
    }

    @Override
    @Transactional
    public NotificationDto markAsSent(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setStatus(NotificationStatus.SENT);
        Notification updated = notificationRepository.save(notification);
        return notificationMapper.toDto(updated);
    }
}
