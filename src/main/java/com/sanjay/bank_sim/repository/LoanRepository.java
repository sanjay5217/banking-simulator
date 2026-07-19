package com.sanjay.bank_sim.repository;

import com.sanjay.bank_sim.model.Loan;
import com.sanjay.bank_sim.model.LoanPayment;
import com.sanjay.bank_sim.model.SchedulePayment;
import com.sanjay.bank_sim.utils.SqlLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class LoanRepository {

    private final JdbcTemplate jdbc;

    public LoanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Loan(
            rs.getInt("loanid"),
            rs.getInt("customerid"),
            rs.getString("type"),
            rs.getBigDecimal("principal"),
            rs.getBigDecimal("interest_rate"),
            rs.getInt("term_months"),
            rs.getObject("start_date", LocalDate.class)
        );
    }

    private LoanPayment mapRowPayment(ResultSet rs, int rowNum) throws SQLException {
        return new LoanPayment(
            rs.getInt("loan_paymentid"),
            rs.getInt("loanid"),
            rs.getObject("payment_date", LocalDate.class),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("principal_portion"),
            rs.getBigDecimal("interest_portion")
        );
    }

    private SchedulePayment mapScheduleRow(ResultSet rs, int rowNum) throws SQLException {
        return new SchedulePayment(
            rs.getInt("payment_num"),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("principal_portion"),
            rs.getBigDecimal("interest_portion"),
            rs.getBigDecimal("remaining_balance"),
            rs.getObject("payment_date", LocalDate.class)
        );
    }

    public Loan findById(int id) {
        String sql = SqlLoader.load("loan/get_by_id.sql");
        return jdbc.query(sql, this::mapRow, id).stream().findFirst().orElseThrow();
    }

    public List<Loan> findByCustomerId(int customerId) {
        String sql = SqlLoader.load("loan/get_by_customer_id.sql");
        return jdbc.query(sql, this::mapRow, customerId);
    }

    public List<SchedulePayment> getLoanSchedule(int loanId) {
        String sql = SqlLoader.load("loan/get_schedule.sql");
        return jdbc.query(sql, this::mapScheduleRow, loanId);
    }

    public int createLoan(int customerId, String type, java.math.BigDecimal principal,
                          java.math.BigDecimal interestRate, int termMonths) {
        String sql = SqlLoader.load("loan/create_loan.sql");
        return jdbc.queryForObject(sql, Integer.class, customerId, type, principal, interestRate, termMonths);
    }
}
