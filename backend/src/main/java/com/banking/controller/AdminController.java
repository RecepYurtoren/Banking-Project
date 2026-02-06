package com.banking.controller;

import com.banking.dto.AccountResponse;
import com.banking.dto.CustomerResponse;
import com.banking.dto.TransactionResponse;
import com.banking.service.AccountService;
import com.banking.service.CustomerService;
import com.banking.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AdminController {

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    // Tüm müşterileri getirme
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Müşteri arama
    @GetMapping("/customers/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    // Müşteri detaylarını getirme
    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // Müşteriyi pasife alma
    @PutMapping("/customers/{id}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long id) {
        customerService.deactivateCustomer(id);
        return ResponseEntity.ok().build();
    }

    // Tüm hesapları ve bakiyeleri getirme
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Müşterinin hesaplarını ve bakiyelerini getirme
    @GetMapping("/customers/{customerId}/accounts")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }

    // ID'si girilen hesabın işlemlerini getirme
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    // Müşterinin işlemlerini getirme
    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getCustomerTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(customerId));
    }

    // İşlem referans numarasıyla getirme
    @GetMapping("/transactions/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(@PathVariable String referenceNumber) {
        TransactionResponse transaction = transactionService.getTransactionByReference(referenceNumber);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    // Tarih aralığına göre işlemleri getirme
    @GetMapping("/accounts/{accountId}/transactions/range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(accountId, startDate, endDate));
    }
}
