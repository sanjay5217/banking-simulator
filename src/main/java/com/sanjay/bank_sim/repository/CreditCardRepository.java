package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CreditCardRepository {
    private final JdbcTemplate jdbc;

    public CreditCardRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private CreditCard mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new CreditCard(
        rs.getInt("creditid"),
        rs.getInt("customerid"),
        rs.getBigDecimal("credit_limit"),
        rs.getBigDecimal("balance"),
        rs.getBigDecimal("apr")
        );
    }

    public List<CreditCard> findByCustomerId(int customerId) {
        String sql = SqlLoader.load("credit-card/get_by_customer.sql");
        return jdbc.query(sql, this::mapRow, customerId);
    }

    public Optional<CreditCard> findById(int creditId) {
        String sql = SqlLoader.load("credit-card/get_by_id.sql");
        return jdbc.query(sql, this::mapRow, creditId).stream().findFirst();
    }

    public int purchase(int creditId, java.math.BigDecimal amount) {
        String sql = SqlLoader.load("credit-card/make_purchase.sql");
        return jdbc.update(sql, amount, creditId, amount);
    }

    public void payCard(int creditId, java.math.BigDecimal amount) {
        String sql = SqlLoader.load("credit-card/pay_card.sql");
        jdbc.update(sql, amount, creditId);
    }
}