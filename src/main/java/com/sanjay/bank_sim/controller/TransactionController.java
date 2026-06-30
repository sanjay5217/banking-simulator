package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.Transaction;
import com.sanjay.bank_sim.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountid}")
    public ResponseEntity<List<Transaction>> getTransactionByAccountId(@PathVariable int accountid) {
        return ResponseEntity.ok(this.transactionService.getByAccountId(accountid));
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<Transaction>> getTransactionByQuery(@PathVariable String query) {
        return ResponseEntity.ok(this.transactionService.getByQuery(query));
    }
}
