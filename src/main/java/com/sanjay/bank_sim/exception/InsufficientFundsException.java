package com.sanjay.bank_sim.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(int accountId) {
        super("Account " + accountId + " has insufficient funds for this operation");
    }
}
