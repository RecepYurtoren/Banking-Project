package com.banking.service;

import com.banking.dto.*;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.CheckingAccount;
import com.banking.model.Customer;
import com.banking.model.SavingsAccount;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Business Logic Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    private ModelMapper modelMapper;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        accountService = new AccountServiceImpl(accountRepository, transactionRepository, customerRepository, modelMapper);
    }

    // Birikim hesabı oluşturma testi
    void testCreateSavingsAccountSuccess() {
        CreateSavingsAccountRequest request = new CreateSavingsAccountRequest();
        request.setAccountHolderName("Ahmet Yilmaz");
        request.setEmail("ahmet@email.com");
        request.setInitialBalance(new BigDecimal("1000.00"));

        Customer customer = new Customer("Ahmet", "Yilmaz", "ahmet@email.com");
        
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any(SavingsAccount.class))).thenAnswer(i -> i.getArgument(0));

        AccountResponse response = accountService.createSavingsAccount(request);

        assertNotNull(response);
        assertEquals("Ahmet Yilmaz", response.getAccountHolderName());
        assertEquals("SAVINGS", response.getAccountType());
        verify(accountRepository, times(1)).save(any(SavingsAccount.class));
    }

    // Hesaba para yatırma işlemi
    @Test
    void testDepositSuccess() {
        
        Long accountId = 1L;
        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("500.00"));
        
        SavingsAccount account = new SavingsAccount("ACC123", "Test", "t@e.com", new BigDecimal("1000.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        TransactionResponse response = accountService.deposit(accountId, request);

        assertEquals(new BigDecimal("1500.00"), account.getBalance());
        assertNotNull(response);
        verify(transactionRepository).save(any());
    }

     // Yetersiz bakiye işlemi testi
    void testWithdrawInsufficientBalance() {
       
        Long accountId = 1L;
        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("2000.00")); // Bakiyeden fazla
        
        SavingsAccount account = new SavingsAccount("ACC123", "Test", "t@e.com", new BigDecimal("1000.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountService.withdraw(accountId, request));
    }

     // İki hesap arasında transfer testi
    @Test
    void testTransferBetweenTwoAccounts() {
       
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("SRC123");
        request.setTargetAccountNumber("TRG456");
        request.setAmount(new BigDecimal("300.00"));

        SavingsAccount source = new SavingsAccount("SRC123", "S", "s@e.com", new BigDecimal("1000.00"));
        SavingsAccount target = new SavingsAccount("TRG456", "R", "r@e.com", new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("SRC123")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("TRG456")).thenReturn(Optional.of(target));

        accountService.transfer(request);

        assertEquals(new BigDecimal("700.00"), source.getBalance());
        assertEquals(new BigDecimal("800.00"), target.getBalance());
        verify(transactionRepository, times(2)).save(any()); // Hem gelen hem giden başarılı şekilde kaydedilmeli.
    }

}