package com.example.ticketflow.domain.Idempotency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_history")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IdempotencyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @CreatedDate
    private LocalDateTime createdAt;

    public IdempotencyHistory(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

}
