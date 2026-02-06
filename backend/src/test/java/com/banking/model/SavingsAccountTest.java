package com.banking.model;

import com.banking.model.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Savings Account Unit Tests")
class SavingsAccountTest {

    private SavingsAccount account;

 // Bir birikim hesabı oluşturuyoruz
    @BeforeEach
    void setUp() {
        account = new SavingsAccount(
            "SAV12345678",
            "Recep Yurtören",
            "ahmet@email.com",
            new BigDecimal("1000.00")
        );
    }

    // Minimum bakiye sınırını zorlamadan para çekme testi
    @Test
    void testWithdrawSuccessWithinLimit() {
        BigDecimal amount = new BigDecimal("800.00");
        account.withdraw(amount);
        assertEquals(new BigDecimal("200.00"), account.getBalance());
    }

     // Bakiyeyi alt sınırın altına düşürecek çekim denemesi
    @Test
    void testWithdrawBelowMinimumBalanceDenied() {
       
        BigDecimal amount = new BigDecimal("950.00");
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            account.withdraw(amount);
        });
        
        assertTrue(exception.getMessage().contains("below minimum balance"));
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    void testDepositSuccess() {
        BigDecimal amount = new BigDecimal("500.00");
        account.deposit(amount);
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
    }


    // %2.5 faiz oranıyla 1000TL için aylık faiz kontrolü
    @Test
    void testCalculateAndApplyInterest() {
     
        BigDecimal expectedInterest = new BigDecimal("2.08"); 
        BigDecimal calculatedInterest = account.calculateMonthlyInterest();
        
        assertEquals(expectedInterest, calculatedInterest);
        
        account.applyMonthlyInterest();
        assertEquals(new BigDecimal("1002.08"), account.getBalance());
    }

    // Para Transfer testi
    @Test
    void testTransferToOtherAccount() {
        SavingsAccount target = new SavingsAccount("SAV999", "Target", "t@e.com", new BigDecimal("500.00"));
        BigDecimal amount = new BigDecimal("400.00");
        
        account.transfer(target, amount);
        
        assertEquals(new BigDecimal("600.00"), account.getBalance());
        assertEquals(new BigDecimal("900.00"), target.getBalance());
    }

    // Hesap pasifken işlem yapılamadığının testi
    @Test
    void testOperationsOnInactiveAccount() {
        account.setActive(false);
        
        assertThrows(IllegalStateException.class, () -> account.withdraw(new BigDecimal("100.00")));
        assertThrows(IllegalStateException.class, () -> account.deposit(new BigDecimal("100.00")));
    }
}