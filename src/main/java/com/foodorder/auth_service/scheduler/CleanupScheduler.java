package com.foodorder.auth_service.scheduler;

import com.foodorder.auth_service.scheduler.service.SchedulerRefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class CleanupScheduler {
    private final SchedulerRefreshTokenService refreshTokenService;

    @Scheduled(cron = "@weekly")
    public void cleanupRefreshTokenTask() {
        refreshTokenService.deleteInvalidToken(Instant.now());
    }
}
