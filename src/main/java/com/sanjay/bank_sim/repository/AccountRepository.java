package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.sanjay.bank_sim.utils.AccountFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbc;

    public AccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AccountFactory.createAccount(rs, rowNum);
    }

    public List<Account> findByCustomerId(int customerId) {
        String sql = SqlLoader.load("account/get_customer_by_id.sql");
        return jdbc.query(sql, this::mapRow, customerId);
    }

    public Optional<Account> findById(int accountId) {
        String sql = SqlLoader.load("account/find_account_by_id.sql");
        return jdbc.query(sql, this::mapRow, accountId).stream().findFirst();
    }

    public void updateBalance(int accountId, BigDecimal changeAmount) {
        String sql = SqlLoader.load("account/update_balance.sql");
        jdbc.update(sql, changeAmount, accountId);
    }

    public void updateTransaction(int accountId, BigDecimal amount, String description) {
        String sql = SqlLoader.load("account/update_transaction.sql");
        jdbc.update(sql, accountId, description, amount);
    }

    public void updateAllInterest() {
        String sql = SqlLoader.load("account/update_all_interest.sql");
        jdbc.update(sql);
    }
}
