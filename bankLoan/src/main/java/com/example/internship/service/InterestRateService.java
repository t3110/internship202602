package com.example.internship.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class InterestRateService {

    private static final BigDecimal DEFAULT_RATE = new BigDecimal("2.0");

    private static final Map<String, BigDecimal> BASE_RATES = Map.of(
            "住宅ローン", new BigDecimal("0.8"),
            "マイカーローン", new BigDecimal("2.5"),
            "教育ローン", new BigDecimal("1.8"),
            "フリーローン", new BigDecimal("4.5")
    );

    public BigDecimal calculate(String loanType, Integer loanPeriod) {
        BigDecimal baseRate = BASE_RATES.getOrDefault(loanType, DEFAULT_RATE);

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

