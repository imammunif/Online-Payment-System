package com.dansmultipro.ops.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_transaction")
public class Transaction extends BaseModel {

    @Column(nullable = false, length = 10, unique = true)
    private String code;

    @Column(nullable = false)
    private BigDecimal totalBill;

    @Column(nullable = false, length = 20)
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "gateway_id", nullable = false)
    private Gateway gateway;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public String getCode() {
        return code;
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public User getCustomer() {
        return customer;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public Product getProduct() {
        return product;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTotalBill(BigDecimal totalBill) {
        this.totalBill = totalBill;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
