package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomer(@PathVariable int customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(accountService.getAccountById(id));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}/summary")
    public ResponseEntity<?> getAccountSummary(@PathVariable int customerId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int id, @RequestBody Map<String, BigDecimal> body) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int id, @RequestBody Map<String, BigDecimal> body) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
