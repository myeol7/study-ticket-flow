package com.example.ticketflow.domain.Idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyHistoryRepository extends JpaRepository<IdempotencyHistory, Long> {

}
