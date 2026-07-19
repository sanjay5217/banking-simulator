package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class TfsaAccount extends Account {
    private final BigDecimal contributionRoom;
    private final BigDecimal annualLimit;

    public TfsaAccount(int id, int customerId, BigDecimal balance,
                       BigDecimal contributionRoom, BigDecimal annualLimit) {
        super(id, customerId, "tfsa", balance);
        this.contributionRoom = contributionRoom;
        this.annualLimit = annualLimit;
    }

    public BigDecimal getContributionRoom() { return contributionRoom; }
    public BigDecimal getAnnualLimit() { return annualLimit; }
}
