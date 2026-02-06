package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.constant.RoleCode;
import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.transactionstatushistory.TransactionStatusHistoryResponseDto;
import com.dansmultipro.ops.exception.InvalidPageException;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.TransactionStatusHistory;
import com.dansmultipro.ops.repository.TransactionStatusHistoryRepo;
import com.dansmultipro.ops.repository.UserRepo;
import com.dansmultipro.ops.service.TransactionStatusHistoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionStatusHistoryServiceImpl extends BaseService implements TransactionStatusHistoryService {

    private final TransactionStatusHistoryRepo transactionStatusHistoryRepo;
    private final UserRepo userRepo;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public TransactionStatusHistoryServiceImpl(TransactionStatusHistoryRepo transactionStatusHistoryRepo, UserRepo userRepo) {
        this.transactionStatusHistoryRepo = transactionStatusHistoryRepo;
        this.userRepo = userRepo;
    }

    @Cacheable(value = "histories", key = "'page:' + #page + 'size:' + #size + 'roleCode:' + #roleCode + 'filterId:' + #filterId")
    @Override
    public PaginatedResponseDto<TransactionStatusHistoryResponseDto> getAll(Integer page, Integer size, String roleCode, String filterId) {
        if (page < 1) {
            throw new InvalidPageException("Invalid requested page, minimum 1");
        }
        if (size < 5) {
            throw new InvalidPageException("Invalid requested page size, minimum 5");
        }

        UUID userId = principalService.getPrincipal().getId();
        userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionStatusHistory> historyPage = null;

        if (RoleCode.SUPERADMIN.getCode().equals(roleCode)) {
            historyPage = transactionStatusHistoryRepo.findByOrderByCreatedAtDesc(pageable);
        }
        if (RoleCode.GATEWAY.getCode().equals(roleCode)) {
            historyPage = transactionStatusHistoryRepo.findAllByTransaction_GatewayIdOrderByCreatedAtDesc(
                    UUID.fromString(filterId),
                    pageable
            );
        }

        List<TransactionStatusHistory> historyList = historyPage.getContent();
        List<TransactionStatusHistoryResponseDto> responseDtoList = new ArrayList<>();
        for (TransactionStatusHistory v : historyList) {
            TransactionStatusHistoryResponseDto responseDto = new TransactionStatusHistoryResponseDto(
                    v.getId(),
                    v.getStatus().getName(),
                    v.getTransaction().getCode(),
                    v.getCreatedAt().format(timeFormat)
            );
            responseDtoList.add(responseDto);
        }

        PaginatedResponseDto<TransactionStatusHistoryResponseDto> paginatedHistoryResponse = new PaginatedResponseDto<>(
                responseDtoList,
                historyPage.getTotalElements()
        );

        return paginatedHistoryResponse;
    }

}