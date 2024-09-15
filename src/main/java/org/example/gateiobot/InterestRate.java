package org.example.gateiobot;

public record InterestRate(String asset,
                           String borrowedAmount,
                           String borrowedAmountUsdt,
                           String borrowAvailable,
                           String borrowAvailableUsdt,
                           String interestRateHour,
                           String interestRateYear,
                           String cryptoLoanFixedRateFor7DayHour,
                           String cryptoLoanFixedRateFor7DayYear,
                           String cryptoLoanFixedRateFor30DayHour,
                           String cryptoLoanFixedRateFor30DayYear) {
}
