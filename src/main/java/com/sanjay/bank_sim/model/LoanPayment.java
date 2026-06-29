package com.sanjay.bank_sim.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanPayment {
    private final int id;
    private final int loanId;
    private final LocalDate paymentDate;
    private final BigDecimal amount;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;

    public LoanPayment(int id, int loanId, LocalDate paymentDate, BigDecimal amount,
                       BigDecimal principalPortion, BigDecimal interestPortion) {
        this.id = id;
        this.loanId = loanId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
    }

    public int getId() { return id; }
    public int getLoanId() { return loanId; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPrincipalPortion() { return principalPortion; }
    public BigDecimal getInterestPortion() { return interestPortion; }
}
