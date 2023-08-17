package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "expiry_date",nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "is_expired",nullable = false)
    private boolean isExpired;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",nullable = false)
    private TokenType type;
}
