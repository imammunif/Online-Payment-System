package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionStatusRepo extends JpaRepository<TransactionStatus, UUID> {

    Optional<TransactionStatus> findByCode(String code);

}
