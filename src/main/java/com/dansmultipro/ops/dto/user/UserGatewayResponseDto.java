package com.dansmultipro.ops.dto.user;

import java.util.UUID;

public class UserGatewayResponseDto {

    private UUID id;
    private String name;
    private String email;
    private String roleName;
    private String gatewayName;
    private String version;

    public UserGatewayResponseDto(UUID id, String name, String email, String roleName, String gatewayName, String version) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
        this.gatewayName = gatewayName;
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

    public String getGatewayName() {
        return gatewayName;
    }

    public String getVersion() {
        return version;
    }

}
