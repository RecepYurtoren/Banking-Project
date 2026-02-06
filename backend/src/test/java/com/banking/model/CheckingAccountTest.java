package com.banking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checking Account Unit Tests")
class CheckingAccountTest {

    private CheckingAccount account;

    @BeforeEach
    void setUp() {
        account = new CheckingAccount("CHK123", "Recep", "recep@email.com", new BigDecimal("500.00"));
    }

     // Para çekme işlemi
    @Test
    void testWithdrawSuccess() {
        account.withdraw(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("200.00"), account.getBalance());
    }

    // Çekim limitini aşan işlem
    @Test
    void testWithdrawExceedLimit() {
       
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.withdraw(new BigDecimal("1100.00"));
        });
        assertTrue(exception.getMessage().contains("exceeds available funds"));
    }

    // Başka bir hesaba transfer yapıldığında bakiyelerin güncellenmesi testi
    @Test
    void testTransferToAnotherAccount() {
        CheckingAccount target = new CheckingAccount("TRG123", "TargetAccount", "target@email.com", new BigDecimal("100.00"));
        account.transfer(target, new BigDecimal("200.00"));
        
        assertEquals(new BigDecimal("300.00"), account.getBalance());
        assertEquals(new BigDecimal("300.00"), target.getBalance()); 
    }

     // Temel para yatırma işleminin kontrolü
    @Test
    void testDepositToCheckingAccount() {
        account.deposit(new BigDecimal("250.00"));
        assertEquals(new BigDecimal("750.00"), account.getBalance());
    }
}