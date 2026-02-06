package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.gateway.GatewayResponseDto;
import com.dansmultipro.ops.service.GatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("gateways")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<List<GatewayResponseDto>> getAll() {
        List<GatewayResponseDto> res = gatewayService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<GatewayResponseDto> getById(
            @PathVariable String id
    ) {
        GatewayResponseDto res = gatewayService.getById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
