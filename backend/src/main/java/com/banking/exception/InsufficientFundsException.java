package com.banking.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    public InsufficientFundsException(String message) {
        super(message);
        this.availableBalance = null;
        this.requestedAmount = null;
    }

    public InsufficientFundsException(String message, BigDecimal availableBalance, BigDecimal requestedAmount) {
        super(message);
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}
