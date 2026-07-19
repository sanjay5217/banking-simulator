package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.Loan;
import com.sanjay.bank_sim.model.SchedulePayment;
import com.sanjay.bank_sim.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean LoanService loanService;

    private Loan loan(int id) {
        return new Loan(id, 1, "personal", new BigDecimal("10000.00"),
                new BigDecimal("0.0899"), 36, LocalDate.of(2025, 1, 1));
    }

    private SchedulePayment payment() {
        return new SchedulePayment(1, new BigDecimal("317.89"), new BigDecimal("242.74"),
                new BigDecimal("75.15"), new BigDecimal("9757.26"), LocalDate.of(2025, 2, 1));
    }

    private static final String VALID_BODY =
            "{\"type\":\"personal\",\"principal\":10000,\"interestRate\":0.0899,\"termMonths\":36}";

    @Test
    void getLoansByCustomer_returns200WithList() throws Exception {
        when(loanService.getLoansByCustomerId(1))
                .thenReturn(List.of(loan(1), loan(2)));

        mockMvc.perform(get("/api/loans/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getLoansByCustomer_noLoans_returns200WithEmptyList() throws Exception {
        when(loanService.getLoansByCustomerId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/loans/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getLoanById_returns200WithLoan() throws Exception {
        when(loanService.getLoanById(1))
                .thenReturn(loan(1));

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("personal")));
    }

    @Test
    void getAmortizationSchedule_returns200WithList() throws Exception {
        when(loanService.getAmortizationSchedule(1))
                .thenReturn(List.of(payment()));

        mockMvc.perform(get("/api/loans/1/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getMonthlyPayment_returns200WithAmount() throws Exception {
        when(loanService.calculateMonthlyPayment(1))
                .thenReturn(new BigDecimal("317.89"));

        mockMvc.perform(get("/api/loans/1/monthly-payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPayment").exists());
    }

    @Test
    void createLoan_validRequest_returns200WithLoan() throws Exception {
        when(loanService.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenReturn(loan(1));

        mockMvc.perform(post("/api/loans/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("personal")));
    }

    @Test
    void createLoan_deniedByRule_returns400WithError() throws Exception {
        when(loanService.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenThrow(new IllegalArgumentException("Loan denied -- unresolved fraud flags on account"));

        mockMvc.perform(post("/api/loans/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("fraud")));
    }

    @Test
    void createLoan_highCreditUtilization_returns400WithError() throws Exception {
        when(loanService.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenThrow(new IllegalArgumentException("Loan denied -- credit card utilization exceeds 75%"));

        mockMvc.perform(post("/api/loans/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("utilization")));
    }
}
