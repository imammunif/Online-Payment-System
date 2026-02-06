package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Transaction> findByGatewayId(UUID gatewayId, Pageable pageable);

    Boolean existsByCustomerId(UUID id);

}
