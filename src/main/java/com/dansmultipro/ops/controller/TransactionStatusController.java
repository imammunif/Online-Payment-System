package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.transactionstatus.TransactionStatusResponseDto;
import com.dansmultipro.ops.service.TransactionStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("transaction-statuses")
public class TransactionStatusController {

    private final TransactionStatusService transactionStatusService;

    public TransactionStatusController(TransactionStatusService transactionStatusService) {
        this.transactionStatusService = transactionStatusService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<List<TransactionStatusResponseDto>> getAll() {
        List<TransactionStatusResponseDto> res = transactionStatusService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
