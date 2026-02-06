package com.banking.repository;

import com.banking.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByAccountHolderName(String accountHolderName);

    List<Account> findByEmail(String email);

    List<Account> findByActive(boolean active);

    List<Account> findByCustomerId(Long customerId);

    @Query("SELECT a FROM Account a WHERE TYPE(a) = com.banking.model.SavingsAccount")
    List<Account> findAllSavingsAccounts();

    @Query("SELECT a FROM Account a WHERE TYPE(a) = com.banking.model.CheckingAccount")
    List<Account> findAllCheckingAccounts();

    @Query("SELECT a FROM Account a WHERE TYPE(a) = :accountType")
    List<Account> findByAccountType(@Param("accountType") Class<? extends Account> accountType);
}
