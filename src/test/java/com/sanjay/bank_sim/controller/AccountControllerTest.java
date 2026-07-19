package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.service.AccountService;
import com.sanjay.bank_sim.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AccountService accountService;
    @MockitoBean TransactionService transactionService;

    private Account account(int id) {
        return new Account(id, 1, "chequing", new BigDecimal("500.00"));
    }

    @Test
    void getAccountsByCustomer_returns200WithList() throws Exception {
        when(accountService.getAccountsByCustomerId(1))
                .thenReturn(List.of(account(1), account(2)));

        mockMvc.perform(get("/api/accounts/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAccountById_existing_returns200() throws Exception {
        when(accountService.getAccountById(1))
                .thenReturn(account(1));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getAccountById_missing_returns404() throws Exception {
        when(accountService.getAccountById(99))
                .thenThrow(new AccountNotFoundException(99));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deposit_validAmount_returns200() throws Exception {
        mockMvc.perform(post("/api/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00}"))
                .andExpect(status().isOk());
    }

    @Test
    void withdraw_validAmount_returns200() throws Exception {
        mockMvc.perform(post("/api/accounts/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountSummary_returns200WithMap() throws Exception {
        HashMap<String, Object> summary = new HashMap<>();
        summary.put("totalIn", new BigDecimal("500.00"));
        summary.put("totalOut", new BigDecimal("200.00"));
        when(transactionService.getTransactionSummary(1)).thenReturn(summary);

        mockMvc.perform(get("/api/accounts/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIn").exists());
    }
}
