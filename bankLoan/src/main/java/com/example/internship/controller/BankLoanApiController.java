package com.example.internship.controller;

import com.example.internship.dto.ScreeningResponse;
import com.example.internship.entity.BankLoanForm;
import com.example.internship.repository.BankLoanRepository;
import com.example.internship.service.ApplyBankLoanService;
import com.example.internship.service.BranchService;
import com.example.internship.service.InterestRateService;
import com.example.internship.service.ScreeningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class BankLoanApiController {

    private static final Logger log = LoggerFactory.getLogger(BankLoanApiController.class);

    @Autowired
    private ApplyBankLoanService applyBankLoanService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private BankLoanRepository bankLoanRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private InterestRateService interestRateService;

    @PostMapping("/saveBankLoan")
    public Map<String, Object> saveBankLoan(@ModelAttribute BankLoanForm bankLoanForm) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("保存データ: bank={}, branch={}, type={}, account={}, name={}, loanType={}, amount={}, income={}, period={}, rate={}",
                    bankLoanForm.getBankName(),
                    bankLoanForm.getBranchName(),
                    bankLoanForm.getBankAccountType(),
                    bankLoanForm.getBankAccountNum(),
                    bankLoanForm.getName(),
                    bankLoanForm.getLoanType(),
                    bankLoanForm.getLoanAmount(),
                    bankLoanForm.getAnnualIncome(),
                    bankLoanForm.getLoanPeriod(),
                    bankLoanForm.getInterestRate());

            applyBankLoanService.applyBankLoan(bankLoanForm);
            response.put("success", true);
            log.info("データ保存成功");
        } catch (Exception e) {
            log.error("データ保存エラー", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/calculateInterestRate")
    public Map<String, Object> calculateInterestRateApi(
            @RequestParam String loanType,
            @RequestParam Integer loanPeriod) {

        BigDecimal interestRate = interestRateService.calculate(loanType, loanPeriod);

        Map<String, Object> response = new HashMap<>();
        response.put("interestRate", interestRate.setScale(2, RoundingMode.HALF_UP).toPlainString());

        return response;
    }

    @GetMapping("/validateBranch")
    public Map<String, Object> validateBranch(@RequestParam String branchName) {
        boolean isValid = branchService.validateBranch(branchName);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        return response;
    }

    @GetMapping("/getAllBranches")
    public Map<String, Object> getAllBranches() {
        Map<String, Object> response = new HashMap<>();
        response.put("branches", branchService.getAllBranches());
        return response;
    }

    @GetMapping("/database-info")
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = bankLoanRepository.count();
            response.put("success", true);
            response.put("tableExists", true);
            response.put("recordCount", count);
            response.put("status", count > 0 ? "データあり" : "テーブルは存在しますがデータは空です");
        } catch (Exception e) {
            response.put("success", false);
            response.put("tableExists", false);
            response.put("error", "bankLoan_tableが見つかりません: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/database-data")
    public Map<String, Object> getDatabaseData() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> data = bankLoanRepository.findAll();
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/screening")
    public Map<String, Object> screening(@ModelAttribute BankLoanForm bankLoanForm) {
        Map<String, Object> response = new HashMap<>();
        ScreeningResponse screeningResponse = screeningService.screen(bankLoanForm);
        response.put("success", true);
        response.put("screening", screeningResponse);
        return response;
    }
}

