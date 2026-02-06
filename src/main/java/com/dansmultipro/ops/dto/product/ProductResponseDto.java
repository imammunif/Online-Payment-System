package com.dansmultipro.ops.dto.product;

import java.util.UUID;

public class ProductResponseDto {

    private UUID id;
    private String name;
    private String code;
    private String version;

    public ProductResponseDto(UUID id, String name, String code, String version) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.version = version;
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

    public String getVersion() {
        return version;
    }

}
