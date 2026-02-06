package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.transactionstatushistory.TransactionStatusHistoryResponseDto;
import com.dansmultipro.ops.service.TransactionStatusHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("histories")
public class TransactionStatusHistoryController {

    private final TransactionStatusHistoryService transactionStatusHistoryService;

    public TransactionStatusHistoryController(TransactionStatusHistoryService transactionStatusHistoryService) {
        this.transactionStatusHistoryService = transactionStatusHistoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'GA')")
    public ResponseEntity<PaginatedResponseDto<TransactionStatusHistoryResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        PaginatedResponseDto<TransactionStatusHistoryResponseDto> res = transactionStatusHistoryService.getAll(page, size);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
