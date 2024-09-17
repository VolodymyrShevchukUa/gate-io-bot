package org.example.gateiobot;

import java.util.Objects;

public record InterestRate(String asset, String borrowedAmount, String borrowedAmountUsdt, String borrowAvailable,
                           String borrowAvailableUsdt, String interestRateHour, String interestRateYear,
                           String cryptoLoanFixedRateFor7DayHour, String cryptoLoanFixedRateFor7DayYear,
                           String cryptoLoanFixedRateFor30DayHour, String cryptoLoanFixedRateFor30DayYear) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestRate that = (InterestRate) o;
        return Objects.equals(interestRateHour, that.interestRateHour) &&
                Objects.equals(interestRateYear, that.interestRateYear) &&
                Objects.equals(cryptoLoanFixedRateFor7DayHour, that.cryptoLoanFixedRateFor7DayHour) &&
                Objects.equals(cryptoLoanFixedRateFor7DayYear, that.cryptoLoanFixedRateFor7DayYear) &&
                Objects.equals(cryptoLoanFixedRateFor30DayHour, that.cryptoLoanFixedRateFor30DayHour) &&
                Objects.equals(cryptoLoanFixedRateFor30DayYear, that.cryptoLoanFixedRateFor30DayYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestRateHour, interestRateYear, cryptoLoanFixedRateFor7DayHour,
                cryptoLoanFixedRateFor7DayYear, cryptoLoanFixedRateFor30DayHour, cryptoLoanFixedRateFor30DayYear);
    }

    @Override
    public String toString() {
        return "InterestRate{" +
                "asset='" + asset + '\'' +
                ", borrowedAmount='" + borrowedAmount + '\'' +
                ", borrowedAmountUsdt='" + borrowedAmountUsdt + '\'' +
                ", borrowAvailable='" + borrowAvailable + '\'' +
                ", borrowAvailableUsdt='" + borrowAvailableUsdt + '\'' +
                ", interestRateHour='" + interestRateHour + '\'' +
                ", interestRateYear='" + interestRateYear + '\'' +
                ", cryptoLoanFixedRateFor7DayHour='" + cryptoLoanFixedRateFor7DayHour + '\'' +
                ", cryptoLoanFixedRateFor7DayYear='" + cryptoLoanFixedRateFor7DayYear + '\'' +
                ", cryptoLoanFixedRateFor30DayHour='" + cryptoLoanFixedRateFor30DayHour + '\'' +
                ", cryptoLoanFixedRateFor30DayYear='" + cryptoLoanFixedRateFor30DayYear + '\'' +
                '}';
    }
}