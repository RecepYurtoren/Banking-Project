package com.banking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InterestCalculationResponse {

    private Long accountId;
    private String accountNumber;
    private BigDecimal balanceBeforeInterest;
    private BigDecimal interestRate;
    private BigDecimal interestAmount;
    private BigDecimal balanceAfterInterest;
    private LocalDateTime calculationDate;
}
