package com.anurastores.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Sale {

    private int saleId;
    private int employeeId;
    private String employeeName;
    private Timestamp saleDate;
    private BigDecimal totalAmount;
    private String saleStatus;
    private String paymentMethod;

    public Sale() {
    }

    public Sale(int saleId,
                int employeeId,
                String employeeName,
                Timestamp saleDate,
                BigDecimal totalAmount,
                String saleStatus,
                String paymentMethod) {

        this.saleId = saleId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.saleStatus = saleStatus;
        this.paymentMethod = paymentMethod;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Timestamp getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Timestamp saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}