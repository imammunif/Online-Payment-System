package com.dansmultipro.ops.dto;

public class UpdateResponseDto {

    private final Integer version;
    private final String message;

    public UpdateResponseDto(Integer version, String message) {
        this.version = version;
        this.message = message;
    }

    public Integer getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

}
