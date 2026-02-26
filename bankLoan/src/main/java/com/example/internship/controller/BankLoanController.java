package com.example.internship.controller;

import com.example.internship.entity.BankLoanForm;
import com.example.internship.service.ApplyBankLoanService;
import com.example.internship.dto.ScreeningResponse;
import com.example.internship.repository.BankLoanRepository;
import com.example.internship.service.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class BankLoanController {

    @Autowired
    private ApplyBankLoanService applyBankLoanService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private BankLoanRepository bankLoanRepository;

    private static final String FIXED_BANK_NAME = "かわらそば銀行";

    private static final Map<String, List<String>> BRANCH_NAMES = new HashMap<String, List<String>>() {{
        put("福岡県", Arrays.asList(
                "福岡支店",
                "博多支店",
                "北九州支店"
        ));
        put("佐賀県", Arrays.asList(
                "佐賀支店",
                "唐津支店",
                "鳥栖支店"
        ));
        put("長崎県", Arrays.asList(
                "長崎支店",
                "佐世保支店",
                "諫早支店"
        ));
        put("熊本県", Arrays.asList(
                "熊本支店",
                "八代支店",
                "天草支店"
        ));
        put("大分県", Arrays.asList(
                "大分支店",
                "別府支店",
                "中津支店"
        ));
        put("宮崎県", Arrays.asList(
                "宮崎支店",
                "都城支店",
                "延岡支店"
        ));
        put("鹿児島県", Arrays.asList(
                "鹿児島支店",
                "霧島支店",
                "薩摩川内支店"
        ));
    }};

    @GetMapping("/bankLoan")
    public String bankTransfer(Model model) {
        BankLoanForm bankLoanForm = new BankLoanForm();
        bankLoanForm.setBankName(FIXED_BANK_NAME);
        model.addAttribute("bankLoanApplication", bankLoanForm);
        model.addAttribute("branchOptions", BRANCH_NAMES);
        return "bankLoanMain";
    }

    @GetMapping("/database-viewer")
    public String databaseViewer() {
        return "database-viewer";
    }

    @PostMapping("/bankLoanConfirmation")
    public String confirmation(@ModelAttribute BankLoanForm bankLoanForm, Model model) {
        BigDecimal interestRate = calculateInterestRate(
                bankLoanForm.getLoanType(),
                bankLoanForm.getLoanPeriod()
        );
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

    @PostMapping("/saveBankLoan")
    @ResponseBody
    public Map<String, Object> saveBankLoan(@ModelAttribute BankLoanForm bankLoanForm) {
        Map<String, Object> response = new HashMap<>();
        try {
            // デバッグ用ログ出力
            System.out.println("保存データ:");
            System.out.println("  銀行名: " + bankLoanForm.getBankName());
            System.out.println("  支店名: " + bankLoanForm.getBranchName());
            System.out.println("  口座種別: " + bankLoanForm.getBankAccountType());
            System.out.println("  口座番号: " + bankLoanForm.getBankAccountNum());
            System.out.println("  名前: " + bankLoanForm.getName());
            System.out.println("  ローンの種類: " + bankLoanForm.getLoanType());
            System.out.println("  借入金額: " + bankLoanForm.getLoanAmount());
            System.out.println("  借入年収: " + bankLoanForm.getAnnualIncome());
            System.out.println("  借入期間: " + bankLoanForm.getLoanPeriod());
            System.out.println("  金利: " + bankLoanForm.getInterestRate());

            // ダイアログでOKが押された後にデータを保存
            applyBankLoanService.applyBankLoan(bankLoanForm);
            response.put("success", true);
            System.out.println("データ保存成功");
        } catch (Exception e) {
            System.err.println("データ保存エラー: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/calculateInterestRate")
    @ResponseBody
    public Map<String, Object> calculateInterestRateApi(
            @RequestParam String loanType,
            @RequestParam Integer loanPeriod) {

        BigDecimal interestRate = calculateInterestRate(loanType, loanPeriod);

        Map<String, Object> response = new HashMap<>();
        response.put("interestRate", interestRate.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

        return response;
    }

    @GetMapping("/validateBranch")
    @ResponseBody
    public Map<String, Object> validateBranch(@RequestParam String branchName) {
        boolean isValid = false;

        // すべての支店リストから検索
        for (List<String> branches : BRANCH_NAMES.values()) {
            if (branches.contains(branchName)) {
                isValid = true;
                break;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        return response;
    }

    @GetMapping("/getAllBranches")
    @ResponseBody
    public Map<String, Object> getAllBranches() {
        Map<String, Object> response = new HashMap<>();
        response.put("branches", BRANCH_NAMES);
        return response;
    }

    @GetMapping("/database-info")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public Map<String, Object> screening(@ModelAttribute BankLoanForm bankLoanForm) {
        Map<String, Object> response = new HashMap<>();
        ScreeningResponse screeningResponse = screeningService.screen(bankLoanForm);
        response.put("success", true);
        response.put("screening", screeningResponse);
        return response;
    }

    private BigDecimal calculateInterestRate(String loanType, Integer loanPeriod) {
        // ローンの種類ごとの基準金利を設定
        BigDecimal baseRate;
        if (loanType == null) {
            baseRate = new BigDecimal("2.0");
        } else {
            switch (loanType) {
                case "住宅ローン":
                    baseRate = new BigDecimal("0.8");
                    break;
                case "マイカーローン":
                    baseRate = new BigDecimal("2.5");
                    break;
                case "教育ローン":
                    baseRate = new BigDecimal("1.8");
                    break;
                case "フリーローン":
                    baseRate = new BigDecimal("4.5");
                    break;
                default:
                    baseRate = new BigDecimal("2.0");
            }
        }

        // 借入期間による金利調整
        if (loanPeriod == null || loanPeriod < 1) {
            return baseRate;
        }

        BigDecimal periodAdjustment;
        if (loanPeriod >= 30) {
            periodAdjustment = new BigDecimal("0.5");
        } else if (loanPeriod >= 20) {
            periodAdjustment = new BigDecimal("0.3");
        } else if (loanPeriod >= 10) {
            periodAdjustment = new BigDecimal("0.1");
        } else if (loanPeriod >= 5) {
            periodAdjustment = new BigDecimal("0.0");
        } else {
            periodAdjustment = new BigDecimal("-0.2");
        }

        return baseRate.add(periodAdjustment);
    }

}
