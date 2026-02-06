package com.banking.service;
import com.banking.dto.*;
import java.util.List;


public interface AccountService {

    AccountResponse createSavingsAccount(CreateSavingsAccountRequest request);
    AccountResponse createCheckingAccount(CreateCheckingAccountRequest request);
    AccountResponse getAccountById(Long id);
    AccountResponse getAccountByNumber(String accountNumber);
    List<AccountResponse> getAllAccounts();
    List<AccountResponse> getActiveAccounts();
    TransactionResponse deposit(Long accountId, DepositRequest request);
    TransactionResponse withdraw(Long accountId, WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    AccountResponse deactivateAccount(Long accountId);
    AccountResponse activateAccount(Long accountId);
    List<AccountResponse> getAccountsByCustomerId(Long customerId);
}
