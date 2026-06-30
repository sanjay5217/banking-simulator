package com.sanjay.bank_sim.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(int accountId) {
        super("Invalid Input");
    }
}