package com.dansmultipro.ops.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_user")
public class User extends BaseModel {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(length = 6)
    private String activationCode;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole userRole;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public Boolean getActive() {
        return isActive;
    }

    public UserRole getUserRole() {
        return userRole;
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

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

}
