package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name length exceeds limit, max 50 characters")
    private String name;

    @Email(message = "Email format is not valid")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email length exceeds limit, max 50 characters")
    private String email;

    @NotNull(message = "Version is required")
    private Integer version;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
