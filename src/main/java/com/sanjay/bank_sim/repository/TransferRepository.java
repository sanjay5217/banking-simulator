package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.model.Transfer;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TransferRepository {

    private final JdbcTemplate jdbc;

    public TransferRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Transfer mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Transfer(
                rs.getInt("transferid"),
                rs.getInt("from_accountId"),
                rs.getInt("to_accountId"),
                rs.getBigDecimal("Amount"),
                rs.getObject("date", LocalDate.class)
        );
    }
    public void transferAmount(int fromAccountId, int toAccountId, BigDecimal amount) {
        String sql = SqlLoader.load("transfer/transfer_amount.sql");
        this.jdbc.update(sql, fromAccountId, toAccountId, amount);
    }

    public List<Transfer> getHistory(int accountId) {
        String sql = SqlLoader.load("transfer/get_history_from_account.sql");
        return this.jdbc.query(sql, this::mapRow, accountId);
    }
}
