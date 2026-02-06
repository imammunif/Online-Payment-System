package com.dansmultipro.ops.dto;

public class DeleteResponseDto {

    private final String message;

    public DeleteResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
