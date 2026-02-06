package com.banking.service.impl;

import com.banking.dto.MonthlyReportResponse;
import com.banking.dto.TransactionResponse;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.ReportService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public ReportServiceImpl(AccountRepository accountRepository, 
                             TransactionRepository transactionRepository,
                             ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    // Generate monthly report for an account
    @Override
    public MonthlyReportResponse generateMonthlyReport(Long accountId, int year, int month) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> AccountNotFoundException.withId(accountId));

        YearMonth reportMonth = YearMonth.of(year, month);
        LocalDateTime startDate = reportMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = reportMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository
            .findByAccountIdAndDateRange(accountId, startDate, endDate);

        BigDecimal totalDeposits = sumByType(accountId, TransactionType.DEPOSIT, startDate, endDate);
        BigDecimal totalWithdrawals = sumByType(accountId, TransactionType.WITHDRAWAL, startDate, endDate);
        BigDecimal totalTransfersIn = sumByType(accountId, TransactionType.TRANSFER_IN, startDate, endDate);
        BigDecimal totalTransfersOut = sumByType(accountId, TransactionType.TRANSFER_OUT, startDate, endDate);
        BigDecimal totalInterest = sumByType(accountId, TransactionType.INTEREST, startDate, endDate);
        BigDecimal totalFees = sumByType(accountId, TransactionType.FEE, startDate, endDate);

        BigDecimal openingBalance = account.getBalance();
        BigDecimal closingBalance = account.getBalance();

        if (!transactions.isEmpty()) {
            Transaction oldestTransaction = transactions.get(transactions.size() - 1);
            openingBalance = oldestTransaction.getBalanceBefore();
            
            Transaction newestTransaction = transactions.get(0);
            closingBalance = newestTransaction.getBalanceAfter();
        }

        List<TransactionResponse> transactionResponses = transactions.stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());

        MonthlyReportResponse response = new MonthlyReportResponse();
        response.setAccountId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountHolderName(account.getAccountHolderName());
        response.setAccountType(account.getAccountType());
        response.setReportMonth(reportMonth);
        response.setOpeningBalance(openingBalance);
        response.setClosingBalance(closingBalance);
        response.setTotalDeposits(totalDeposits);
        response.setTotalWithdrawals(totalWithdrawals);
        response.setTotalTransfersIn(totalTransfersIn);
        response.setTotalTransfersOut(totalTransfersOut);
        response.setTotalInterestEarned(totalInterest);
        response.setTotalFeesCharged(totalFees);
        response.setTransactionCount(transactions.size());
        response.setTransactions(transactionResponses);
        return response;
    }

    @Override
    public MonthlyReportResponse generateMonthlyReportByAccountNumber(String accountNumber, int year, int month) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> AccountNotFoundException.withAccountNumber(accountNumber));
        return generateMonthlyReport(account.getId(), year, month);
    }

    private BigDecimal sumByType(Long accountId, TransactionType type, 
            LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal sum = transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
            accountId, type, startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
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
