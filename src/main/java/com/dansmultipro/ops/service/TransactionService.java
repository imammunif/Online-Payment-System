package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.transaction.CreateTransactionRequestDto;
import com.dansmultipro.ops.dto.transaction.TransactionResponseDto;

public interface TransactionService {

    PaginatedResponseDto<TransactionResponseDto> getAll(Integer page, Integer size, String roleCode, String filterId);

    CreateResponseDto create(CreateTransactionRequestDto data);

    UpdateResponseDto update(String id, String action, Integer version);

}
