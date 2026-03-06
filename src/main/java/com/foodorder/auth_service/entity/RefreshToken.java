package com.foodorder.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String tokenHash;
    private Instant createdAt;
    private Instant expiredAt;

    @Setter
    @Column(nullable = false)
    private boolean revoked = false;

    public RefreshToken(Long userId, String tokenHash, Instant createdAt, Instant expiredAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}
