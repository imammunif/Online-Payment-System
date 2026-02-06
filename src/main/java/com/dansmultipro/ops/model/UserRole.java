package com.dansmultipro.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_m_user_role")
public class UserRole extends BaseModel {

    @Column(nullable = false, length = 5, unique = true)
    private String code;

    @Column(nullable = false, length = 20)
    private String name;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

}
