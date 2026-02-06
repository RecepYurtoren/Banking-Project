package com.banking.service;

import com.banking.dto.MonthlyReportResponse;

public interface ReportService {

    MonthlyReportResponse generateMonthlyReport(Long accountId, int year, int month);

    MonthlyReportResponse generateMonthlyReportByAccountNumber(String accountNumber, int year, int month);
}
