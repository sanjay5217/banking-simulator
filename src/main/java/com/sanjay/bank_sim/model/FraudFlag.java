package com.sanjay.bank_sim.model;

import java.time.LocalDateTime;

public class FraudFlag {
    private final int id;
    private final int transactionId;
    private final String reason;
    private final LocalDateTime flaggedAt;
    private final boolean resolved;

    public FraudFlag(int id, int transactionId, String reason, LocalDateTime flaggedAt, boolean resolved) {
        this.id = id;
        this.transactionId = transactionId;
        this.reason = reason;
        this.flaggedAt = flaggedAt;
        this.resolved = resolved;
    }

    public int getId() { return id; }
    public int getTransactionId() { return transactionId; }
    public String getReason() { return reason; }
    public LocalDateTime getFlaggedAt() { return flaggedAt; }
    public boolean isResolved() { return resolved; }
}
