package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.Transaction;
import com.sanjay.bank_sim.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean TransactionService transactionService;

    private Transaction txn(int id) {
        return new Transaction(id, 1, LocalDate.of(2025, 3, 1), "Deposit", new BigDecimal("100.00"), null);
    }

    @Test
    void getByAccountId_noMonth_returns200WithAllTransactions() throws Exception {
        when(transactionService.getByAccountId(1, null))
                .thenReturn(List.of(txn(1), txn(2)));

        mockMvc.perform(get("/api/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getByAccountId_noTransactions_returns200WithEmptyList() throws Exception {
        when(transactionService.getByAccountId(1, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getByAccountId_withMonth_returns200WithFilteredList() throws Exception {
        when(transactionService.getByAccountId(1, "2025-03"))
                .thenReturn(List.of(txn(1)));

        mockMvc.perform(get("/api/transaction/1").param("month", "2025-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getByAccountId_withMonthNoMatch_returns200WithEmptyList() throws Exception {
        when(transactionService.getByAccountId(1, "2020-01"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transaction/1").param("month", "2020-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    void searchByQuery_matchingTransactions_returns200WithList() throws Exception {
        when(transactionService.getByQuery("Deposit"))
                .thenReturn(List.of(txn(1)));

        mockMvc.perform(get("/api/transaction/search/Deposit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchByQuery_noMatch_returns200WithEmptyList() throws Exception {
        when(transactionService.getByQuery("xyz"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transaction/search/xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
