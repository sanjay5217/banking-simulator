package com.sanjay.bank_sim.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan {
    private final int id;
    private final int customerId;
    private final String type;
    private final BigDecimal principal;
    private final BigDecimal interestRate;
    private final int termMonths;
    private final LocalDate startDate;

    public Loan(int id, int customerId, String type, BigDecimal principal,
                BigDecimal interestRate, int termMonths, LocalDate startDate) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.principal = principal;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.startDate = startDate;
    }

    public int getId() { return this.id; }
    public int getCustomerId() { return this.customerId; }
    public String getType() { return this.type; }
    public BigDecimal getPrincipal() { return this.principal; }
    public BigDecimal getInterestRate() { return this.interestRate; }
    public int getTermMonths() { return this.termMonths; }
    public LocalDate getStartDate() { return this.startDate; }
}
