package com.dansmultipro.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_m_gateway")
public class Gateway extends BaseModel {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 10, unique = true)
    private String code;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
