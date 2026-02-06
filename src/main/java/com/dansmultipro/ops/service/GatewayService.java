package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.gateway.GatewayResponseDto;

import java.util.List;

public interface GatewayService {

    List<GatewayResponseDto> getAll();

    GatewayResponseDto getById(String id);

}
