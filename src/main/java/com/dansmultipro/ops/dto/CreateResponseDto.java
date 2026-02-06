package com.dansmultipro.ops.dto;

import java.util.UUID;

public class CreateResponseDto {

    private UUID id;
    private String message;

    public CreateResponseDto(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
