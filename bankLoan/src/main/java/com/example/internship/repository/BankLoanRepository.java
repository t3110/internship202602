package com.example.internship.repository;

import com.example.internship.entity.BankLoanForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class BankLoanRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void create(BankLoanForm bankLoanForm) {
        String sql = "INSERT INTO bankLoan_table(bankName, branchName, bankAccountType, bankAccountNum, name, loanType, loanAmount, annualIncome, loanPeriod, interestRate) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                bankLoanForm.getBankName(),
                bankLoanForm.getBranchName(),
                bankLoanForm.getBankAccountType(),
                bankLoanForm.getBankAccountNum(),
                bankLoanForm.getName(),
                bankLoanForm.getLoanType(),
                bankLoanForm.getLoanAmount(),
                bankLoanForm.getAnnualIncome(),
                bankLoanForm.getLoanPeriod(),
                bankLoanForm.getInterestRate()
        );
    }

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM bankLoan_table";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            return List.of();
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM bankLoan_table";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class);
        } catch (Exception e) {
            return 0L;
        }
    }

}
