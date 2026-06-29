package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class ChequingAccount extends Account {
    private final BigDecimal overdraftLimit;
    private final int freeTransactionLimit;
    private final BigDecimal monthlyFee;

    public ChequingAccount(int id, int customerId, BigDecimal balance,
                           BigDecimal overdraftLimit, int freeTransactionLimit, BigDecimal monthlyFee) {
        super(id, customerId, "chequing", balance);
        this.overdraftLimit = overdraftLimit;
        this.freeTransactionLimit = freeTransactionLimit;
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
    public int getFreeTransactionLimit() { return freeTransactionLimit; }
    public BigDecimal getMonthlyFee() { return monthlyFee; }
}
