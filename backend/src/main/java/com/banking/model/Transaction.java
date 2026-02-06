package com.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_account", columnList = "account_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type")
})
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "related_account_number", length = 20)
    private String relatedAccountNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "reference_number", unique = true, length = 50)
    private String referenceNumber;

    @PrePersist
    protected void onCreate() {
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
        if (this.referenceNumber == null) {
            this.referenceNumber = generateReferenceNumber();
        }
    }

    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    public Transaction(Account account, TransactionType type, BigDecimal amount,
                      BigDecimal balanceBefore, BigDecimal balanceAfter, String description) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }

    public static Transaction createDeposit(Account account, BigDecimal amount,
                                           BigDecimal balanceBefore, BigDecimal balanceAfter) {
        return new Transaction(account, TransactionType.DEPOSIT, amount, balanceBefore, balanceAfter, "Deposit");
    }

    public static Transaction createWithdrawal(Account account, BigDecimal amount,
                                              BigDecimal balanceBefore, BigDecimal balanceAfter) {
        return new Transaction(account, TransactionType.WITHDRAWAL, amount, balanceBefore, balanceAfter, "Withdrawal");
    }

    public static Transaction createTransferOut(Account account, BigDecimal amount,
                                               BigDecimal balanceBefore, BigDecimal balanceAfter,
                                               String targetAccountNumber) {
        Transaction transaction = new Transaction(account, TransactionType.TRANSFER_OUT, amount,
            balanceBefore, balanceAfter, "Transfer to " + targetAccountNumber);
        transaction.setRelatedAccountNumber(targetAccountNumber);
        return transaction;
    }

    public static Transaction createTransferIn(Account account, BigDecimal amount,
                                              BigDecimal balanceBefore, BigDecimal balanceAfter,
                                              String sourceAccountNumber) {
        Transaction transaction = new Transaction(account, TransactionType.TRANSFER_IN, amount,
            balanceBefore, balanceAfter, "Transfer from " + sourceAccountNumber);
        transaction.setRelatedAccountNumber(sourceAccountNumber);
        return transaction;
    }

    public static Transaction createInterest(Account account, BigDecimal amount,
                                            BigDecimal balanceBefore, BigDecimal balanceAfter) {
        return new Transaction(account, TransactionType.INTEREST, amount, balanceBefore, balanceAfter, "Monthly interest payment");
    }

    public static Transaction createFee(Account account, BigDecimal amount,
                                       BigDecimal balanceBefore, BigDecimal balanceAfter,
                                       String feeDescription) {
        return new Transaction(account, TransactionType.FEE, amount, balanceBefore, balanceAfter, feeDescription);
    }
}
