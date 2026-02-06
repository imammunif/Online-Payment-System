package com.dansmultipro.ops.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_gateway_user")
public class GatewayUser extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "gateway_id", nullable = false)
    private Gateway gateway;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Gateway getGateway() {
        return gateway;
    }

    public User getUser() {
        return user;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
