package com.dansmultipro.ops.dto.login;

public class LoginResponseDto {

    private String name;
    private String roleCode;
    private String token;

    public LoginResponseDto(String name, String roleCode, String token) {
        this.name = name;
        this.roleCode = roleCode;
        this.token = token;
    }

    public String getFullName() {
        return name;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getToken() {
        return token;
    }

}
