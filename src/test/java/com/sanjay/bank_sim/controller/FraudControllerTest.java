package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.model.FraudFlag;
import com.sanjay.bank_sim.service.FraudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FraudController.class)
class FraudControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean FraudService fraudService;

    private FraudFlag flag(int id, boolean resolved) {
        return new FraudFlag(id, 1, "Large transaction", LocalDateTime.of(2025, 3, 1, 10, 0), resolved);
    }

    @Test
    void getFlagsByCustomer_returns200WithList() throws Exception {
        when(fraudService.getFlagsByCustomerId(1))
                .thenReturn(List.of(flag(1, false), flag(2, true)));

        mockMvc.perform(get("/api/fraud/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getFlagsByCustomer_noFlags_returns200WithEmptyList() throws Exception {
        when(fraudService.getFlagsByCustomerId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/fraud/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUnresolvedFlags_returns200WithList() throws Exception {
        when(fraudService.getUnresolvedFlags())
                .thenReturn(List.of(flag(1, false)));

        mockMvc.perform(get("/api/fraud/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUnresolvedFlags_noneUnresolved_returns200WithEmptyList() throws Exception {
        when(fraudService.getUnresolvedFlags())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/fraud/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void resolveFlag_returns200AndCallsService() throws Exception {
        mockMvc.perform(post("/api/fraud/1/resolve"))
                .andExpect(status().isOk());

        verify(fraudService).resolveFlag(1);
    }

    @Test
    void detectFraud_suspiciousActivity_returns200WithFlaggedList() throws Exception {
        when(fraudService.detectAndFlagSuspicious(1))
                .thenReturn(List.of(flag(1, false)));

        mockMvc.perform(post("/api/fraud/detect/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void detectFraud_nothingSuspicious_returns200WithEmptyList() throws Exception {
        when(fraudService.detectAndFlagSuspicious(1))
                .thenReturn(List.of());

        mockMvc.perform(post("/api/fraud/detect/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
