package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Customer;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbc;

    public CustomerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
            rs.getInt("customerid"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getObject("date_of_birth", LocalDate.class),
            rs.getObject("member_since", LocalDate.class)
        );
    }

    public List<Customer> findAll() {
        String sql = SqlLoader.load("customers/get_all_customers.sql");
        return jdbc.query(sql, this::mapRow);
    }

    public Optional<Customer> findById(int id) {
        String sql = SqlLoader.load("customers/find_by_id.sql");
        return jdbc.query(sql, this::mapRow, id).stream().findFirst();
    }

    public List<Customer> searchByName(String name) {
        String sql = SqlLoader.load("customers/search_by_name.sql");
        return jdbc.query(sql, this::mapRow, "%" + name + "%");
    }
}
