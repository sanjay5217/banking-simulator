package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.FraudFlag;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FraudRepository {
    private final JdbcTemplate jdbc;

    public FraudRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private FraudFlag mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FraudFlag(
            rs.getInt("fraud_flagid"),
            rs.getInt("transactionid"),
            rs.getString("reason"),
            rs.getDate("flagged_date").toLocalDate().atStartOfDay(),
            rs.getBoolean("resolved")
        );
    }

    public List<FraudFlag> findByCustomerId(int customerId) {
        String sql = SqlLoader.load("fraud/get_by_customer.sql");
        return jdbc.query(sql, this::mapRow, customerId);
    }

    public List<FraudFlag> findUnresolved() {
        String sql = SqlLoader.load("fraud/get_unresolved.sql");
        return jdbc.query(sql, this::mapRow);
    }

    public void resolve(int flagId) {
        String sql = SqlLoader.load("fraud/resolve.sql");
        jdbc.update(sql, flagId);
    }

    public List<FraudFlag> detectAndInsert(int accountId) {
        String sql = SqlLoader.load("fraud/detect_and_insert.sql");
        return jdbc.query(sql, this::mapRow, accountId, accountId);
    }
}
