package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.service.CreditCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditCardController.class)
class CreditCardControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CreditCardService creditCardService;

    private CreditCard card(int id) {
        return new CreditCard(id, 1, new BigDecimal("5000.00"), new BigDecimal("1200.00"), new BigDecimal("0.1999"));
    }

    @Test
    void getByCustomer_returns200WithList() throws Exception {
        when(creditCardService.getByCustomer(1))
                .thenReturn(List.of(card(1)));

        mockMvc.perform(get("/api/credit-cards/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getByCustomer_noCards_returns200WithEmptyList() throws Exception {
        when(creditCardService.getByCustomer(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/credit-cards/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getById_existing_returns200() throws Exception {
        when(creditCardService.getById(1))
                .thenReturn(card(1));

        mockMvc.perform(get("/api/credit-cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(creditCardService.getById(99))
                .thenThrow(new AccountNotFoundException(99));

        mockMvc.perform(get("/api/credit-cards/99"))
                .andExpect(status().isNotFound());
    }
}
