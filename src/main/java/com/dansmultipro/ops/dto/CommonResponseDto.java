package com.dansmultipro.ops.dto;

public class CommonResponseDto {

    private final String message;

    public CommonResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
