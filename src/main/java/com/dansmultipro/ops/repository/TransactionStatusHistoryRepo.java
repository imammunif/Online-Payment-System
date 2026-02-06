package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.TransactionStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionStatusHistoryRepo extends JpaRepository<TransactionStatusHistory, UUID> {

    Page<TransactionStatusHistory> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<TransactionStatusHistory> findAllByTransaction_GatewayIdOrderByCreatedAtDesc(UUID gatewayId, Pageable pageable);

}
