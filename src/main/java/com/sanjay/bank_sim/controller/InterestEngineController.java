package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.service.InterestEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interest-engine")
public class InterestEngineController {

    private final InterestEngineService interestEngineService;

    public InterestEngineController(InterestEngineService interestEngineService) {
        this.interestEngineService = interestEngineService;
    }

    @PostMapping("/run")
    public ResponseEntity<?> updateInterest() {
        this.interestEngineService.updateInterest();
        return ResponseEntity.ok().build();
    }
}