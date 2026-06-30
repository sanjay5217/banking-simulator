package com.sanjay.bank_sim.exception;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }
}
