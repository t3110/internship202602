package com.example.internship.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BranchService {

    private static final String FIXED_BANK_NAME = "かわらそば銀行";

    private static final Map<String, List<String>> BRANCH_NAMES = new LinkedHashMap<>();

    static {
        BRANCH_NAMES.put("福岡県", List.of(
                "福岡支店",
                "博多支店",
                "北九州支店"
        ));
        BRANCH_NAMES.put("佐賀県", List.of(
                "佐賀支店",
                "唐津支店",
                "鳥栖支店"
        ));
        BRANCH_NAMES.put("長崎県", List.of(
                "長崎支店",
                "佐世保支店",
                "諫早支店"
        ));
        BRANCH_NAMES.put("熊本県", List.of(
                "熊本支店",
                "八代支店",
                "天草支店"
        ));
        BRANCH_NAMES.put("大分県", List.of(
                "大分支店",
                "別府支店",
                "中津支店"
        ));
        BRANCH_NAMES.put("宮崎県", List.of(
                "宮崎支店",
                "都城支店",
                "延岡支店"
        ));
        BRANCH_NAMES.put("鹿児島県", List.of(
                "鹿児島支店",
                "霧島支店",
                "薩摩川内支店"
        ));
    }

    public String getFixedBankName() {
        return FIXED_BANK_NAME;
    }

    public Map<String, List<String>> getBranchNames() {
        return BRANCH_NAMES;
    }

    public Map<String, List<String>> getAllBranches() {
        return BRANCH_NAMES;
    }

    public boolean validateBranch(String branchName) {
        if (branchName == null || branchName.isBlank()) {
            return false;
        }
        return BRANCH_NAMES.values().stream().anyMatch(branches -> branches.contains(branchName));
    }
}

