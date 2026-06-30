package com.sanjay.bank_sim.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(int customerId) {
        super("Customer not found: " + customerId);
    }
}
