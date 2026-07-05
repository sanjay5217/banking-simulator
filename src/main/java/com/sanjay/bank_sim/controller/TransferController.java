package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.exception.TransferException;
import com.sanjay.bank_sim.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/{toAccountId}")
    public ResponseEntity<?> transferAmount(@PathVariable int toAccountId, @RequestBody Map<String, BigDecimal> body) {
        int fromAccountId = body.get("fromAccountId").intValue();
        this.transferService.transfer(fromAccountId, toAccountId, body.get("amount"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary/{accountId}")
    public ResponseEntity<?> transferHistory(@PathVariable int accountId) {
        return ResponseEntity.ok(this.transferService.getTransferHistory(accountId));
    }

}
