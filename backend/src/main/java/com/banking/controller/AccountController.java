package com.banking.controller;

import com.banking.dto.*;
import com.banking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/savings")
    public ResponseEntity<AccountResponse> createSavingsAccount(
            @Valid @RequestBody CreateSavingsAccountRequest request) {
        AccountResponse response = accountService.createSavingsAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/checking")
    public ResponseEntity<AccountResponse> createCheckingAccount(
            @Valid @RequestBody CreateCheckingAccountRequest request) {
        AccountResponse response = accountService.createCheckingAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse response = accountService.deposit(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request) {
        TransactionResponse response = accountService.withdraw(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(@PathVariable Long id) {
        AccountResponse response = accountService.deactivateAccount(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activateAccount(@PathVariable Long id) {
        AccountResponse response = accountService.activateAccount(id);
        return ResponseEntity.ok(response);
    }
}
