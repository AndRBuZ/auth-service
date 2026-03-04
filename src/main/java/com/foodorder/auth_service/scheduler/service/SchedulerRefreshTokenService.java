package com.foodorder.auth_service.scheduler.service;

import com.foodorder.auth_service.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class SchedulerRefreshTokenService {
    private final RefreshTokenRepository repository;

    @Transactional
    public void deleteInvalidToken(Instant now) {
        repository.deleteInvalidTokens(now);
    }
}
