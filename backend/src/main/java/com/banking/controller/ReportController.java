package com.banking.controller;

import com.banking.dto.InterestCalculationResponse;
import com.banking.dto.MonthlyReportResponse;
import com.banking.service.InterestService;
import com.banking.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Report and interest endpoints
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;
    private final InterestService interestService;

    public ReportController(ReportService reportService, InterestService interestService) {
        this.reportService = reportService;
        this.interestService = interestService;
    }

    // Get monthly report by account ID
    @GetMapping("/monthly/{accountId}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable Long accountId,
            @RequestParam int year,
            @RequestParam int month) {
        MonthlyReportResponse report = reportService.generateMonthlyReport(accountId, year, month);
        return ResponseEntity.ok(report);
    }

    // Get monthly report by account number
    @GetMapping("/monthly/account/{accountNumber}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReportByAccountNumber(
            @PathVariable String accountNumber,
            @RequestParam int year,
            @RequestParam int month) {
        MonthlyReportResponse report = reportService.generateMonthlyReportByAccountNumber(
            accountNumber, year, month);
        return ResponseEntity.ok(report);
    }

    // Calculate interest without applying
    @GetMapping("/interest/calculate/{accountId}")
    public ResponseEntity<InterestCalculationResponse> calculateInterest(@PathVariable Long accountId) {
        InterestCalculationResponse calculation = interestService.calculateInterest(accountId);
        return ResponseEntity.ok(calculation);
    }

    // Apply interest to account
    @PostMapping("/interest/apply/{accountId}")
    public ResponseEntity<InterestCalculationResponse> applyInterest(@PathVariable Long accountId) {
        InterestCalculationResponse result = interestService.applyInterest(accountId);
        return ResponseEntity.ok(result);
    }

    // Apply interest to all savings accounts
    @PostMapping("/interest/apply-all")
    public ResponseEntity<List<InterestCalculationResponse>> applyInterestToAllAccounts() {
        List<InterestCalculationResponse> results = interestService.triggerInterestApplication();
        return ResponseEntity.ok(results);
    }
}
