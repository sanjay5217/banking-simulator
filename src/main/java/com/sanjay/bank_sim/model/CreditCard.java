package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class CreditCard {
    private final int id;
    private final int customerId;
    private final BigDecimal limit;
    private final BigDecimal balance;
    private final BigDecimal apr;

    public CreditCard(int id, int customerId, BigDecimal limit, BigDecimal balance, BigDecimal apr) {
        this.id = id;
        this.customerId = customerId;
        this.limit = limit;
        this.balance = balance;
        this.apr = apr;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public BigDecimal getLimit() { return limit; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getApr() { return apr; }
}
