package com.dansmultipro.ops.dto.gateway;

import java.util.UUID;

public class GatewayResponseDto {

    private UUID id;
    private String name;
    private String code;

    public GatewayResponseDto(UUID id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

}
