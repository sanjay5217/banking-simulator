package com.sanjay.bank_sim.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(int accountId) {
        super("Account not found: " + accountId);
    }
}
