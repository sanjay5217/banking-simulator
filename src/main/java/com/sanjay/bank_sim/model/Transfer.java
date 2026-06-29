package com.sanjay.bank_sim.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transfer {
    private final int id;
    private final int fromAccountId;
    private final int toAccountId;
    private final BigDecimal amount;
    private final LocalDate date;

    public Transfer(int id, int fromAccountId, int toAccountId, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public int getFromAccountId() { return fromAccountId; }
    public int getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
