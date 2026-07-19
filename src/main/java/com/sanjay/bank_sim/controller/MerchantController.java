package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.Merchant;
import com.sanjay.bank_sim.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping
    public ResponseEntity<List<Merchant>> getAll() {
        return ResponseEntity.ok(merchantService.getAll());
    }
}
