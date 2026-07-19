package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.Transfer;
import com.sanjay.bank_sim.service.TransferService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean TransferService transferService;

    private Transfer transfer(int id) {
        return new Transfer(id, 1, 2, new BigDecimal("100.00"), LocalDate.of(2025, 3, 1));
    }

    @Test
    void transfer_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/api/transfer/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountId\": 1, \"amount\": 50.00}"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransferHistory_returns200WithList() throws Exception {
        when(transferService.getTransferHistory(1))
                .thenReturn(List.of(transfer(1), transfer(2)));

        mockMvc.perform(get("/api/transfer/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getTransferHistory_noHistory_returns200WithEmptyList() throws Exception {
        when(transferService.getTransferHistory(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transfer/summary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
