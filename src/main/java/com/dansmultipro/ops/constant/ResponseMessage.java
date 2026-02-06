package com.dansmultipro.ops.constant;

public enum ResponseMessage {

    CREATED("Created"),
    UPDATED("Updated"),
    DELETED("Deleted");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}