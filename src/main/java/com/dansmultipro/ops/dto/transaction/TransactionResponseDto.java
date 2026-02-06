package com.dansmultipro.ops.dto.transaction;

public class TransactionResponseDto {

    private String id;
    private String code;
    private String totalBill;
    private String accountNumber;
    private String statusName;
    private String customerName;
    private String gatewayName;
    private String productName;

    public TransactionResponseDto(String id, String code, String totalBill, String virtualNumber, String statusName, String customerName, String gatewayName, String productName) {
        this.id = id;
        this.code = code;
        this.totalBill = totalBill;
        this.accountNumber = virtualNumber;
        this.statusName = statusName;
        this.customerName = customerName;
        this.gatewayName = gatewayName;
        this.productName = productName;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getProductName() {
        return productName;
    }

}
