package com.banking.service;

import com.banking.dto.TransactionResponse;
import com.banking.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for transaction operations.
 */
public interface TransactionService {

    List<TransactionResponse> getTransactionsByAccountId(Long accountId);

    Page<TransactionResponse> getTransactionsByAccountId(Long accountId, Pageable pageable);

    List<TransactionResponse> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<TransactionResponse> getTransactionsByType(Long accountId, TransactionType type);

    TransactionResponse getTransactionByReference(String referenceNumber);

    List<TransactionResponse> getMonthlyTransactions(Long accountId, int year, int month);

    long countTransactions(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<TransactionResponse> getTransactionsByCustomerId(Long customerId);
}
