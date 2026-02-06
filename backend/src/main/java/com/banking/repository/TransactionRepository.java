package com.banking.repository;

import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
 
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    List<Transaction> findByAccountIdAndType(Long accountId, TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountIdAndDateRange(
        @Param("accountId") Long accountId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByAccountIdAndTypeAndDateRange(
        @Param("accountId") Long accountId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    long countByAccountIdAndDateRange(
        @Param("accountId") Long accountId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND YEAR(t.transactionDate) = :year AND MONTH(t.transactionDate) = :month " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findMonthlyTransactions(
        @Param("accountId") Long accountId,
        @Param("year") int year,
        @Param("month") int month
    );


    @Query("SELECT t FROM Transaction t WHERE t.type = 'INTEREST' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findInterestTransactionsInRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

 
    @Query("SELECT t FROM Transaction t WHERE t.type = 'FEE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findFeeTransactionsInRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
