package com.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "checking_accounts")
@Getter
@Setter
@NoArgsConstructor
public class CheckingAccount extends Account implements Transferable {

    public static final BigDecimal DEFAULT_OVERDRAFT_LIMIT = new BigDecimal("500.00");
    public static final BigDecimal DEFAULT_MONTHLY_FEE = new BigDecimal("10.00");

    @Column(name = "overdraft_limit", precision = 15, scale = 2)
    private BigDecimal overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;

    @Column(name = "monthly_fee", precision = 10, scale = 2)
    private BigDecimal monthlyFee = DEFAULT_MONTHLY_FEE;

    public CheckingAccount(String accountNumber, String accountHolderName, String email, BigDecimal initialBalance) {
        super(accountNumber, accountHolderName, email, initialBalance);
    }

    public CheckingAccount(String accountNumber, String accountHolderName, String email,
                          BigDecimal initialBalance, BigDecimal overdraftLimit, BigDecimal monthlyFee) {
        super(accountNumber, accountHolderName, email, initialBalance);
        this.overdraftLimit = overdraftLimit != null ? overdraftLimit : DEFAULT_OVERDRAFT_LIMIT;
        this.monthlyFee = monthlyFee != null ? monthlyFee : DEFAULT_MONTHLY_FEE;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        validateAccountActive();
        validatePositiveAmount(amount);

        BigDecimal newBalance = getBalance().subtract(amount);
        BigDecimal minimumAllowedBalance = overdraftLimit.negate();
        
        if (newBalance.compareTo(minimumAllowedBalance) < 0) {
            throw new IllegalStateException(
                String.format("Withdrawal denied: Amount (%.2f) exceeds available funds including overdraft limit (%.2f). " +
                    "Current balance: %.2f, Overdraft limit: %.2f",
                    amount, getBalance().add(overdraftLimit), getBalance(), overdraftLimit)
            );
        }
        
        setBalance(newBalance);
    }

    @Override
    public void transfer(Account targetAccount, BigDecimal amount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }
        if (targetAccount.getId() != null && targetAccount.getId().equals(this.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        this.withdraw(amount);
        targetAccount.deposit(amount);
    }

    public BigDecimal getAvailableBalance() {
        return getBalance().add(overdraftLimit);
    }

    public boolean isInOverdraft() {
        return getBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal applyMonthlyFee() {
        if (monthlyFee.compareTo(BigDecimal.ZERO) > 0) {
            withdraw(monthlyFee);
        }
        return monthlyFee;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}
