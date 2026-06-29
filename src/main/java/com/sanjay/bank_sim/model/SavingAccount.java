package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class SavingAccount extends Account {
    private final BigDecimal interestRate;
    private final BigDecimal minBalance;

    public SavingAccount(int id, int customerId, BigDecimal balance,
                         BigDecimal interestRate, BigDecimal minBalance) {
        super(id, customerId, "savings", balance);
        this.interestRate = interestRate;
        this.minBalance = minBalance;
    }

    public BigDecimal getInterestRate() { return interestRate; }
    public BigDecimal getMinBalance() { return minBalance; }
}
