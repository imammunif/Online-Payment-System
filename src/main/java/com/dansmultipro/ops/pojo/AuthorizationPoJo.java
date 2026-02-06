package com.dansmultipro.ops.pojo;

import java.util.UUID;

public class AuthorizationPoJo {

    private UUID id;
    private String roleCode;

    public AuthorizationPoJo(UUID id, String roleCode) {
        this.id = id;
        this.roleCode = roleCode;
    }

    public UUID getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

}
