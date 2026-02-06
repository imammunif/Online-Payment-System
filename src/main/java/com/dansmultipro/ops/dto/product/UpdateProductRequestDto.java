package com.dansmultipro.ops.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateProductRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 35, message = "Name length exceeds limit, max 35 characters")
    private String name;

    @NotNull(message = "Version is required")
    private Integer version;

    public String getName() {
        return name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
