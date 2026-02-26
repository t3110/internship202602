package com.example.internship.controller;

import com.example.internship.entity.BankLoanForm;
import com.example.internship.service.BranchService;
import com.example.internship.service.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Controller
public class BankLoanController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private InterestRateService interestRateService;

    @GetMapping("/bankLoan")
    public String bankTransfer(Model model) {
        BankLoanForm bankLoanForm = new BankLoanForm();
        bankLoanForm.setBankName(branchService.getFixedBankName());
        model.addAttribute("bankLoanApplication", bankLoanForm);
        model.addAttribute("branchOptions", branchService.getBranchNames());
        return "bankLoanMain";
    }

    @GetMapping("/database-viewer")
    public String databaseViewer() {
        return "database-viewer";
    }

    @PostMapping("/bankLoanConfirmation")
    public String confirmation(@ModelAttribute BankLoanForm bankLoanForm, Model model) {
        BigDecimal interestRate = interestRateService.calculate(bankLoanForm.getLoanType(), bankLoanForm.getLoanPeriod());
        bankLoanForm.setInterestRate(interestRate);
        model.addAttribute("bankLoanApplication", bankLoanForm);
        return "bankLoanConfirmation";
    }

    @PostMapping("/bankLoanCompletion")
    public String completion(@ModelAttribute BankLoanForm bankLoanForm, Model model) {
        // データは保存せず、完了画面の表示のみ
        model.addAttribute("bankLoanApplication", bankLoanForm);
        return "bankLoanCompletion";
    }

    @GetMapping("/bankLoanCompletion")
    public String completionGet() {
        // JavaScript経由で遷移された場合の完了画面表示
        return "bankLoanCompletion";
    }
}
