package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.gateway.GatewayResponseDto;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.Gateway;
import com.dansmultipro.ops.repository.GatewayRepo;
import com.dansmultipro.ops.service.GatewayService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GatewayServiceImpl extends BaseService implements GatewayService {

    private final GatewayRepo gatewayRepo;

    public GatewayServiceImpl(GatewayRepo gatewayRepo) {
        this.gatewayRepo = gatewayRepo;
    }

    @Override
    public List<GatewayResponseDto> getAll() {
        List<GatewayResponseDto> result = gatewayRepo.findAll().stream()
                .map(v -> new GatewayResponseDto(v.getId(), v.getName(), v.getCode()))
                .toList();
        return result;
    }

    @Override
    public GatewayResponseDto getById(String id) {
        UUID validId = validateUUID(id);
        Gateway gateway = gatewayRepo.findById(validId).orElseThrow(
                () -> new NotFoundException("Gateway not found")
        );
        return new GatewayResponseDto(gateway.getId(), gateway.getName(), gateway.getCode());
    }

}
