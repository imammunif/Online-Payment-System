package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.transactionstatus.TransactionStatusResponseDto;
import com.dansmultipro.ops.repository.TransactionStatusRepo;
import com.dansmultipro.ops.service.TransactionStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionStatusServiceImpl extends BaseService implements TransactionStatusService {

    private final TransactionStatusRepo transactionStatusRepo;

    public TransactionStatusServiceImpl(TransactionStatusRepo transactionStatusRepo) {
        this.transactionStatusRepo = transactionStatusRepo;
    }

    @Override
    public List<TransactionStatusResponseDto> getAll() {
        List<TransactionStatusResponseDto> result = transactionStatusRepo.findAll().stream()
                .map(v -> new TransactionStatusResponseDto(v.getId(), v.getName(), v.getCode()))
                .toList();
        return result;
    }

}
