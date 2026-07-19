package com.sanjay.bank_sim.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SchedulePayment {
    private final int paymentNum;
    private final BigDecimal amount;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;
    private final BigDecimal remainingBalance;
    private final LocalDate paymentDate;

    public SchedulePayment(int paymentNum, BigDecimal amount, BigDecimal principalPortion,
                           BigDecimal interestPortion, BigDecimal remainingBalance, LocalDate paymentDate) {
        this.paymentNum = paymentNum;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.remainingBalance = remainingBalance;
        this.paymentDate = paymentDate;
    }

    public int getPaymentNum() { return paymentNum; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPrincipalPortion() { return principalPortion; }
    public BigDecimal getInterestPortion() { return interestPortion; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public LocalDate getPaymentDate() { return paymentDate; }
}
