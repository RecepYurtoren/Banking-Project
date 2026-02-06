package com.banking.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public static AccountNotFoundException withId(Long accountId) {
        return new AccountNotFoundException("Account not found with ID: " + accountId);
    }

    public static AccountNotFoundException withAccountNumber(String accountNumber) {
        return new AccountNotFoundException("Account not found with number: " + accountNumber);
    }
}
