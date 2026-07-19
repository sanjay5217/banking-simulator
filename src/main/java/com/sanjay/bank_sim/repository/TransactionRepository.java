package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Transaction;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbc;

    public TransactionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Transaction(
            rs.getInt("transactionid"),
            rs.getInt("accountid"),
            rs.getObject("date", LocalDate.class),
            rs.getString("description"), 
            rs.getBigDecimal("amount"), 
            rs.getInt("merchantId")
        );
    }

    private HashMap<String, Object> convertTransactions(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, Object> transactionMap = new HashMap<>();
        transactionMap.put("openingBalance", rs.getBigDecimal("opening_balance"));
        transactionMap.put("closingBalance", rs.getBigDecimal("closing_balance"));
        transactionMap.put("transactions", rs.getInt("transactions"));
        transactionMap.put("totalAmount", rs.getBigDecimal("total_amount"));
        transactionMap.put("cashIn", rs.getBigDecimal("cash_in"));
        transactionMap.put("cashOut", rs.getBigDecimal("cash_out"));
        return transactionMap;
    }

    public List<Transaction> findByAccountId(int accountid) {
        String sql = SqlLoader.load("transaction/get_by_account_id.sql");
        return this.jdbc.query(sql, this::mapRow, accountid);
    }

    public List<Transaction> findByAccountIdAndMonth(int accountId, String month) {
        String sql = SqlLoader.load("transaction/get_by_account_id_month.sql");
        return this.jdbc.query(sql, this::mapRow, accountId, month);
    }

    public List<Transaction> findByQuery(String query) {
        String sql = SqlLoader.load("transaction/get_by_query.sql");
        String query_sql = "%" + query + "%";
        return this.jdbc.query(sql, this::mapRow, query_sql);
    }

    public HashMap<String, Object> getTransactionSummary(int accountId) {
        String sql = SqlLoader.load("transaction/transaction_summary.sql");
        return this.jdbc.query(sql, this::convertTransactions, accountId).getFirst();
    }
}
