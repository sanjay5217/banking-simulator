package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getLoansByCustomer(@PathVariable int customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable int id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> getAmortizationSchedule(@PathVariable int id) {
        return ResponseEntity.ok(loanService.getAmortizationSchedule(id));
    }

    @GetMapping("/{id}/monthly-payment")
    public ResponseEntity<?> getMonthlyPayment(@PathVariable int id) {
        return ResponseEntity.ok(Map.of("monthlyPayment", loanService.calculateMonthlyPayment(id)));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<?> createLoan(@PathVariable int customerId, @RequestBody Map<String, Object> body) {
        try {
            String type = body.get("type").toString();
            BigDecimal principal = new BigDecimal(body.get("principal").toString());
            BigDecimal interestRate = new BigDecimal(body.get("interestRate").toString());
            int termMonths = Integer.parseInt(body.get("termMonths").toString());
            return ResponseEntity.ok(loanService.createLoan(customerId, type, principal, interestRate, termMonths));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
