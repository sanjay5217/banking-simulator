package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.service.AccountService;
import com.sanjay.bank_sim.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
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

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int accountId, @RequestBody Map<String, BigDecimal> body) {
        this.accountService.deposit(accountId, body.get("amount"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int accountId, @RequestBody Map<String, BigDecimal> body) {
        this.accountService.withdraw(accountId, body.get("amount"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/summary")
    public ResponseEntity<?> getAccountSummary(@PathVariable int accountId) {
        HashMap<String, Object> transactionMap = this.transactionService.getTransactionSummary(accountId);
        return ResponseEntity.ok(transactionMap);
    }
}
