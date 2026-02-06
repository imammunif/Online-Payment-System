package com.dansmultipro.ops.dto.transactionstatushistory;

import java.util.UUID;

public class TransactionStatusHistoryResponseDto {

    private UUID id;
    private String statusName;
    private String transactionCode;
    private String createdAt;

    public TransactionStatusHistoryResponseDto() {
    }

    public TransactionStatusHistoryResponseDto(UUID id, String statusName, String ticketCode, String createdAt) {
        this.id = id;
        this.statusName = statusName;
        this.transactionCode = ticketCode;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
