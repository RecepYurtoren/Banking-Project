package com.banking.service.impl;

import com.banking.dto.InterestCalculationResponse;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.InterestService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InterestServiceImpl implements InterestService {

    private static final Logger logger = LoggerFactory.getLogger(InterestServiceImpl.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public InterestServiceImpl(AccountRepository accountRepository, 
                               TransactionRepository transactionRepository,
                               ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    // Apply interest to a savings account
    @Override
    public InterestCalculationResponse applyInterest(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        if (!(account instanceof SavingsAccount savingsAccount)) {
            throw new IllegalStateException("Interest can only be applied to savings accounts");
        }

        if (!account.isActive()) {
            throw new IllegalStateException("Cannot apply interest to inactive account");
        }

        BigDecimal balanceBefore = savingsAccount.getBalance();
        BigDecimal interestAmount = savingsAccount.calculateMonthlyInterest();

        if (interestAmount.compareTo(BigDecimal.ZERO) > 0) {
            savingsAccount.deposit(interestAmount);
            accountRepository.save(savingsAccount);

            Transaction transaction = Transaction.createInterest(
                savingsAccount,
                interestAmount,
                balanceBefore,
                savingsAccount.getBalance()
            );
            transactionRepository.save(transaction);
        }

        InterestCalculationResponse response = new InterestCalculationResponse();
        response.setAccountId(savingsAccount.getId());
        response.setAccountNumber(savingsAccount.getAccountNumber());
        response.setBalanceBeforeInterest(balanceBefore);
        response.setInterestRate(savingsAccount.getInterestRate());
        response.setInterestAmount(interestAmount);
        response.setBalanceAfterInterest(savingsAccount.getBalance());
        response.setCalculationDate(LocalDateTime.now());
        return response;
    }

    // Calculate interest without applying
    @Override
    @Transactional(readOnly = true)
    public InterestCalculationResponse calculateInterest(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        if (!(account instanceof SavingsAccount savingsAccount)) {
            throw new IllegalStateException("Interest can only be calculated for savings accounts");
        }

        BigDecimal interestAmount = savingsAccount.calculateMonthlyInterest();

        InterestCalculationResponse response = new InterestCalculationResponse();
        response.setAccountId(savingsAccount.getId());
        response.setAccountNumber(savingsAccount.getAccountNumber());
        response.setBalanceBeforeInterest(savingsAccount.getBalance());
        response.setInterestRate(savingsAccount.getInterestRate());
        response.setInterestAmount(interestAmount);
        response.setBalanceAfterInterest(savingsAccount.getBalance().add(interestAmount));
        response.setCalculationDate(LocalDateTime.now());
        return response;
    }

    // Monthly scheduled interest application
    @Override
    @Scheduled(cron = "0 0 0 1 * *")
    public List<InterestCalculationResponse> applyMonthlyInterestToAllAccounts() {
        logger.info("Starting monthly interest application for all savings accounts");
        
        List<Account> savingsAccounts = accountRepository.findAllSavingsAccounts();
        List<InterestCalculationResponse> results = new ArrayList<>();

        for (Account account : savingsAccounts) {
            if (account.isActive() && account instanceof SavingsAccount) {
                try {
                    InterestCalculationResponse result = applyInterest(account.getId());
                    results.add(result);
                    logger.info("Applied interest of {} to account {}", 
                        result.getInterestAmount(), result.getAccountNumber());
                } catch (Exception e) {
                    logger.error("Failed to apply interest to account {}: {}", 
                        account.getAccountNumber(), e.getMessage());
                }
            }
        }

        logger.info("Completed monthly interest application. Processed {} accounts", results.size());
        return results;
    }

    @Override
    public List<InterestCalculationResponse> triggerInterestApplication() {
        return applyMonthlyInterestToAllAccounts();
    }
}
