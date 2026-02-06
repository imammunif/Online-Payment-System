package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.transactionstatushistory.TransactionStatusHistoryResponseDto;

public interface TransactionStatusHistoryService {

    PaginatedResponseDto<TransactionStatusHistoryResponseDto> getAll(Integer page, Integer size);

}
