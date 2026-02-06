package com.banking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountHolderName;
    private String email;
    private BigDecimal balance;
    private String accountType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private BigDecimal overdraftLimit;
    private BigDecimal monthlyFee;
    private BigDecimal availableBalance;
}
