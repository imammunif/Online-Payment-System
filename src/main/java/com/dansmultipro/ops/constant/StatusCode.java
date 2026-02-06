package com.dansmultipro.ops.constant;

public enum StatusCode {

    PROCESS("PROCESS"),
    PAID("PAY"),
    REJECTED("REJECT");

    private final String code;

    StatusCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
