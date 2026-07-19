package com.sanjay.bank_sim.model;

import java.math.BigDecimal;

public class RrspAccount extends Account {
    private final BigDecimal contributionRoom;
    private final BigDecimal annualLimit;
    private final int maturityYear;

    public RrspAccount(int id, int customerId, BigDecimal balance,
                       BigDecimal contributionRoom, BigDecimal annualLimit, int maturityYear) {
        super(id, customerId, "rrsp", balance);
        this.contributionRoom = contributionRoom;
        this.annualLimit = annualLimit;
        this.maturityYear = maturityYear;
    }

    public BigDecimal getContributionRoom() { return contributionRoom; }
    public BigDecimal getAnnualLimit() { return annualLimit; }
    public int getMaturityYear() { return maturityYear; }
}
