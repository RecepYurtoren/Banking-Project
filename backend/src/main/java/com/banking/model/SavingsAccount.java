package com.banking.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "savings_accounts")
@Getter
@Setter
@NoArgsConstructor
public class SavingsAccount extends Account implements Transferable {

    public static final BigDecimal DEFAULT_MINIMUM_BALANCE = new BigDecimal("100.00");
    public static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("2.5");

    @Column(name = "minimum_balance", precision = 15, scale = 2)
    private BigDecimal minimumBalance = DEFAULT_MINIMUM_BALANCE;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = DEFAULT_INTEREST_RATE;

    public SavingsAccount(String accountNumber, String accountHolderName, String email, BigDecimal initialBalance) {
        super(accountNumber, accountHolderName, email, initialBalance);
    }

    public SavingsAccount(String accountNumber, String accountHolderName, String email, 
                         BigDecimal initialBalance, BigDecimal minimumBalance, BigDecimal interestRate) {
        super(accountNumber, accountHolderName, email, initialBalance);
        this.minimumBalance = minimumBalance != null ? minimumBalance : DEFAULT_MINIMUM_BALANCE;
        this.interestRate = interestRate != null ? interestRate : DEFAULT_INTEREST_RATE;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        validateAccountActive();
        validatePositiveAmount(amount);

        BigDecimal newBalance = getBalance().subtract(amount);
        
        if (newBalance.compareTo(minimumBalance) < 0) {
            throw new IllegalStateException(
                String.format("Withdrawal denied: Resulting balance (%.2f) would be below minimum balance (%.2f)",
                    newBalance, minimumBalance)
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

    public BigDecimal calculateMonthlyInterest() {
        return getBalance()
            .multiply(interestRate)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
            .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal applyMonthlyInterest() {
        BigDecimal interest = calculateMonthlyInterest();
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            deposit(interest);
        }
        return interest;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}
