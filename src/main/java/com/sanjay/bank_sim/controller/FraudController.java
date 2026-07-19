package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.service.FraudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/fraud")
public class FraudController {

    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getFlagsByCustomer(@PathVariable int customerId) {
        return ResponseEntity.ok(fraudService.getFlagsByCustomerId(customerId));
    }

    @GetMapping("/unresolved")
    public ResponseEntity<?> getUnresolvedFlags() {
        return ResponseEntity.ok(fraudService.getUnresolvedFlags());
    }

    @PostMapping("/{flagId}/resolve")
    public ResponseEntity<?> resolveFlag(@PathVariable int flagId) {
        fraudService.resolveFlag(flagId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/detect/{accountId}")
    public ResponseEntity<?> detectFraud(@PathVariable int accountId) {
        return ResponseEntity.ok(fraudService.detectAndFlagSuspicious(accountId));
    }
}
