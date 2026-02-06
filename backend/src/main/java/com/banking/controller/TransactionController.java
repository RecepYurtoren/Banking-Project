package com.banking.controller;

import com.banking.dto.TransactionResponse;
import com.banking.model.TransactionType;
import com.banking.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// Transaction query endpoints
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get all transactions for an account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(
            @PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Get paginated transactions
    @GetMapping("/account/{accountId}/paged")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountPaged(
            @PathVariable Long accountId,
            Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    // Get transactions by date range
    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TransactionResponse> transactions = 
            transactionService.getTransactionsByDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    // Get transactions by type
    @GetMapping("/account/{accountId}/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            @PathVariable Long accountId,
            @PathVariable TransactionType type) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByType(accountId, type);
        return ResponseEntity.ok(transactions);
    }

    // Get transaction by reference number
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable String referenceNumber) {
        TransactionResponse transaction = transactionService.getTransactionByReference(referenceNumber);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    // Get monthly transactions
    @GetMapping("/account/{accountId}/monthly")
    public ResponseEntity<List<TransactionResponse>> getMonthlyTransactions(
            @PathVariable Long accountId,
            @RequestParam int year,
            @RequestParam int month) {
        List<TransactionResponse> transactions = 
            transactionService.getMonthlyTransactions(accountId, year, month);
        return ResponseEntity.ok(transactions);
    }
}
