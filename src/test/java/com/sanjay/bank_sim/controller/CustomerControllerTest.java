package com.sanjay.bank_sim.controller;

import com.sanjay.bank_sim.exception.CustomerNotFoundException;
import com.sanjay.bank_sim.model.Customer;
import com.sanjay.bank_sim.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CustomerService customerService;

    private Customer customer(int id, String name) {
        return new Customer(id, name, "test@email.com", "555-0000",
                LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));
    }

    @Test
    void getAllCustomers_returns200WithList() throws Exception {
        when(customerService.getAllCustomers())
                .thenReturn(List.of(customer(1, "Alice"), customer(2, "Bob")));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCustomerById_existing_returns200() throws Exception {
        when(customerService.getCustomerById(1))
                .thenReturn(customer(1, "Alice"));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    void getCustomerById_missing_returns404() throws Exception {
        when(customerService.getCustomerById(99))
                .thenThrow(new CustomerNotFoundException(99));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchByName_matchingCustomers_returns200WithList() throws Exception {
        when(customerService.searchByName("ali"))
                .thenReturn(List.of(customer(1, "Alice")));

        mockMvc.perform(get("/api/customers/search").param("name", "ali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchByName_noMatch_returns200WithEmptyList() throws Exception {
        when(customerService.searchByName("xyz"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/customers/search").param("name", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
