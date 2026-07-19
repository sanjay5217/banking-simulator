package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.service.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditCard>> getByCustomer(@PathVariable int customerId) {
        return ResponseEntity.ok(creditCardService.getByCustomer(customerId));
    }

    @GetMapping("/{creditId}")
    public ResponseEntity<CreditCard> getById(@PathVariable int creditId) {
        try {
            return ResponseEntity.ok(creditCardService.getById(creditId));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{creditId}/purchase")
    public ResponseEntity<?> purchase(@PathVariable int creditId, @RequestBody Map<String, Object> body) {
        try {
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            creditCardService.purchase(creditId, amount);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{creditId}/minimum-payment")
    public ResponseEntity<?> minimumPayment(@PathVariable int creditId) {
        try {
            return ResponseEntity.ok(creditCardService.minimumPayment(creditId));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{creditId}/pay")
    public ResponseEntity<?> payCard(@PathVariable int creditId, @RequestBody Map<String, Object> body) {
        try {
            int fromAccountId = Integer.parseInt(body.get("fromAccountId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            creditCardService.payCard(creditId, fromAccountId, amount);
            return ResponseEntity.ok().build();
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Insufficient funds"));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
