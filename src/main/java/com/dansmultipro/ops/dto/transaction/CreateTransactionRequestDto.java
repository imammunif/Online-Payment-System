package com.dansmultipro.ops.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateTransactionRequestDto {

    @NotNull(message = "Account number is required")
    @Min(value = 0, message = "Account cannot be negative")
    private String accountNumber;

    @NotNull(message = "Transaction amount is required")
    @Min(value = 5000, message = "Amount minimum at least 5000")
    private BigDecimal amount;

    @NotBlank(message = "Gateway is required")
    private String gatewayId;

    @NotBlank(message = "Product is required")
    private String productId;

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getProductId() {
        return productId;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
