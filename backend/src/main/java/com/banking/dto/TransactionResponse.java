package com.banking.dto;

import com.banking.model.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private String referenceNumber;
    private String accountNumber;
    private TransactionType type;
    private String typeDisplayName;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String relatedAccountNumber;
    private LocalDateTime transactionDate;
    private boolean credit;
}
