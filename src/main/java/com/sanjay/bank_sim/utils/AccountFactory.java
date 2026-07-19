package com.sanjay.bank_sim.utils;

import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.model.ChequingAccount;
import com.sanjay.bank_sim.model.SavingAccount;
import com.sanjay.bank_sim.model.TfsaAccount;
import com.sanjay.bank_sim.model.RrspAccount;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountFactory {
    public static Account createAccount(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("accountid");
        int customerId = rs.getInt("customerid");
        String type = rs.getString("type");
        BigDecimal balance = rs.getBigDecimal("balance");

        return switch (type) {
            case "chequing" -> {
                BigDecimal overdraftLimit = rs.getBigDecimal("overdraft_limit");
                int freeTransactionLimit = rs.getInt("free_transaction_limit");
                BigDecimal monthlyFee = rs.getBigDecimal("monthly_fee");
                yield new ChequingAccount(id, customerId, balance, overdraftLimit, freeTransactionLimit, monthlyFee);
            }
            case "savings" -> {
                BigDecimal interestRate = rs.getBigDecimal("interest_rate");
                BigDecimal minBalance = rs.getBigDecimal("min_balance");
                yield new SavingAccount(id, customerId, balance, interestRate, minBalance);
            }
            case "tfsa" -> {
                BigDecimal contributionRoom = rs.getBigDecimal("contribution_room");
                BigDecimal annualLimit = rs.getBigDecimal("annual_limit");
                yield new TfsaAccount(id, customerId, balance, contributionRoom, annualLimit);
            }
            case "rrsp" -> {
                BigDecimal contributionRoom = rs.getBigDecimal("contribution_room");
                BigDecimal annualLimit = rs.getBigDecimal("annual_limit");
                int maturityYear = rs.getInt("maturity_year");
                yield new RrspAccount(id, customerId, balance, contributionRoom, annualLimit, maturityYear);
            }
            default -> new Account(id, customerId, type, balance);
        };
    }
}
