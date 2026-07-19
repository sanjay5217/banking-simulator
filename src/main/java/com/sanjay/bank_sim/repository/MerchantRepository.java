package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Merchant;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MerchantRepository {

    private final JdbcTemplate jdbc;

    public MerchantRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private Merchant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Merchant(
            rs.getInt("merchantid"),
            rs.getString("name"),
            rs.getString("category")
        );
    }

    public List<Merchant> findAll() {
        String sql = SqlLoader.load("merchant/get_all.sql");
        return jdbc.query(sql, this::mapRow);
    }
}
