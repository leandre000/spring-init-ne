package com.utility.billing.mapper;

import com.utility.billing.dto.UserDto;
import com.utility.billing.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
