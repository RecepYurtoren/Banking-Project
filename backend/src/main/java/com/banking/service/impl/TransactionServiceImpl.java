package com.banking.service.impl;

import com.banking.dto.TransactionResponse;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, 
                                  AccountRepository accountRepository,
                                  ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        validateAccountExists(accountId);
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId).stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> getTransactionsByAccountId(Long accountId, Pageable pageable) {
        validateAccountExists(accountId);
        return transactionRepository.findByAccountId(accountId, pageable)
            .map(this::mapToTransactionResponse);
    }

    @Override
    public List<TransactionResponse> getTransactionsByDateRange(Long accountId, 
            LocalDateTime startDate, LocalDateTime endDate) {
        validateAccountExists(accountId);
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate).stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByType(Long accountId, TransactionType type) {
        validateAccountExists(accountId);
        return transactionRepository.findByAccountIdAndType(accountId, type).stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionByReference(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber)
            .map(this::mapToTransactionResponse)
            .orElse(null);
    }

    @Override
    public List<TransactionResponse> getMonthlyTransactions(Long accountId, int year, int month) {
        validateAccountExists(accountId);
        return transactionRepository.findMonthlyTransactions(accountId, year, month).stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public long countTransactions(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        validateAccountExists(accountId);
        return transactionRepository.countByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    @Override
    public List<TransactionResponse> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    private void validateAccountExists(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw AccountNotFoundException.withId(accountId);
        }
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setAccountNumber(transaction.getAccount().getAccountNumber());
        response.setType(transaction.getType());
        response.setTypeDisplayName(transaction.getType().getDisplayName());
        response.setAmount(transaction.getAmount());
        response.setBalanceBefore(transaction.getBalanceBefore());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setRelatedAccountNumber(transaction.getRelatedAccountNumber());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setCredit(transaction.getType().isCredit());
        return response;
    }
}
