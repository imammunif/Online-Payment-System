package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.transactionstatus.TransactionStatusResponseDto;

import java.util.List;

public interface TransactionStatusService {

    List<TransactionStatusResponseDto> getAll();

}
