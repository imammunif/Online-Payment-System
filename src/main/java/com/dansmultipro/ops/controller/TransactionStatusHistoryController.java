package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.transactionstatushistory.TransactionStatusHistoryResponseDto;
import com.dansmultipro.ops.service.PrincipalService;
import com.dansmultipro.ops.service.TransactionStatusHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("histories")
public class TransactionStatusHistoryController {

    private final TransactionStatusHistoryService transactionStatusHistoryService;
    private final PrincipalService principalService;

    public TransactionStatusHistoryController(TransactionStatusHistoryService transactionStatusHistoryService, PrincipalService principalService) {
        this.transactionStatusHistoryService = transactionStatusHistoryService;
        this.principalService = principalService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'GA')")
    public ResponseEntity<PaginatedResponseDto<TransactionStatusHistoryResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        String roleCode = principalService.getPrincipal().getRoleCode();
        UUID userId = principalService.getPrincipal().getId();
        String filterId = principalService.getFilterId(userId, roleCode);

        PaginatedResponseDto<TransactionStatusHistoryResponseDto> res = transactionStatusHistoryService.getAll(page, size, roleCode, filterId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
