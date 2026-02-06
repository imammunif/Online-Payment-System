package com.dansmultipro.ops.dto.user;

import java.util.UUID;

public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
    private String roleName;
    private String isActive;
    private String version;

    public UserResponseDto() {}

    public UserResponseDto(UUID id, String name, String email, String roleName, String isActive, String version) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
        this.isActive = isActive;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getIsActive() {
        return isActive;
    }

    public String getVersion() {
        return version;
    }

}
