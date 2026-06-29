package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class Account {
    private final int id;
    private final int customerId;
    private final String type;
    private final BigDecimal balance;

    public Account(int id, int customerId, String type, BigDecimal balance) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public String getType() { return type; }
    public BigDecimal getBalance() { return balance; }
}
