package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class CreditCard {
    private final int id;
    private final int customerId;
    private final BigDecimal creditLimit;
    private final BigDecimal balance;
    private final BigDecimal apr;

    public CreditCard(int id, int customerId, BigDecimal creditLimit, BigDecimal balance, BigDecimal apr) {
        this.id = id;
        this.customerId = customerId;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.apr = apr;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getApr() { return apr; }
}
