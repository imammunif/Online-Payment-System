package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.transaction.CreateTransactionRequestDto;
import com.dansmultipro.ops.dto.transaction.TransactionResponseDto;
import com.dansmultipro.ops.service.PrincipalService;
import com.dansmultipro.ops.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final PrincipalService principalService;

    public TransactionController(TransactionService transactionService, PrincipalService principalService) {
        this.transactionService = transactionService;
        this.principalService = principalService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'CUST', 'GA')")
    public ResponseEntity<PaginatedResponseDto<TransactionResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        String roleCode = principalService.getPrincipal().getRoleCode();

        UUID userId = principalService.getPrincipal().getId();
        String filterId = principalService.getFilterId(userId, roleCode);

        PaginatedResponseDto<TransactionResponseDto> res = transactionService.getAll(page, size, roleCode, filterId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUST')")
    public ResponseEntity<CreateResponseDto> create(
            @RequestBody @Valid CreateTransactionRequestDto data
    ) {
        CreateResponseDto res = transactionService.create(data);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('GA')")
    @PutMapping
    public ResponseEntity<UpdateResponseDto> update(
            @RequestParam String id,
            @RequestParam String action,
            @RequestParam Integer version
    ) {
        UpdateResponseDto res = transactionService.update(id, action, version);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
