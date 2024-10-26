package com.map.friends.friend_map.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl  implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Lấy thông tin người dùng hiện tại từ Spring Security Context
        // Ví dụ: trả về username của người dùng đăng nhập
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
