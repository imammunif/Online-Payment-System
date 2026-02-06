package com.dansmultipro.ops.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserCustomerRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name length exceeds limit, max 50 characters")
    private String name;

    @Email(message = "Email format is not valid")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email length exceeds limit, max 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Size(max = 200, message = "Password length exceeds limit, max 200 characters")
    private String password;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
