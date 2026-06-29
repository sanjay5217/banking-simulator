package com.sanjay.bank_sim.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private final int id;
    private final int accountId;
    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;
    private final Integer merchantId;

    public Transaction(int id, int accountId, LocalDate date, String description,
                       BigDecimal amount, Integer merchantId) {
        this.id = id;
        this.accountId = accountId;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.merchantId = merchantId;
    }

    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public Integer getMerchantId() { return merchantId; }
}
