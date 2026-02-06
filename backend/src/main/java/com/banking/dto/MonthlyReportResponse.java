package com.banking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyReportResponse {

    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private String accountType;
    private YearMonth reportMonth;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfersIn;
    private BigDecimal totalTransfersOut;
    private BigDecimal totalInterestEarned;
    private BigDecimal totalFeesCharged;
    private int transactionCount;
    private List<TransactionResponse> transactions;

    public BigDecimal getNetChange() {
        return closingBalance.subtract(openingBalance);
    }

    public BigDecimal getTotalCredits() {
        return totalDeposits.add(totalTransfersIn).add(totalInterestEarned);
    }

    public BigDecimal getTotalDebits() {
        return totalWithdrawals.add(totalTransfersOut).add(totalFeesCharged);
    }
}
