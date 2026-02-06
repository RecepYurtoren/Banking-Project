package com.banking.service.impl;

import com.banking.dto.*;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.*;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public AccountServiceImpl(AccountRepository accountRepository, 
                              TransactionRepository transactionRepository,
                              CustomerRepository customerRepository,
                              ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountResponse createSavingsAccount(CreateSavingsAccountRequest request) {
        String accountNumber = generateAccountNumber();
        Customer customer = findOrCreateCustomer(request.getAccountHolderName(), request.getEmail());
        
        SavingsAccount account = new SavingsAccount(
            accountNumber,
            request.getAccountHolderName(),
            request.getEmail(),
            request.getInitialBalance(),
            request.getMinimumBalance(),
            request.getInterestRate()
        );
        account.setCustomer(customer);
        
        Account savedAccount = accountRepository.save(account);
        return mapToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse createCheckingAccount(CreateCheckingAccountRequest request) {
        String accountNumber = generateAccountNumber();
        Customer customer = findOrCreateCustomer(request.getAccountHolderName(), request.getEmail());
        
        CheckingAccount account = new CheckingAccount(
            accountNumber,
            request.getAccountHolderName(),
            request.getEmail(),
            request.getInitialBalance(),
            request.getOverdraftLimit(),
            request.getMonthlyFee()
        );
        account.setCustomer(customer);
        
        Account savedAccount = accountRepository.save(account);
        return mapToAccountResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Account account = findAccountById(id);
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
            .map(this::mapToAccountResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        return accountRepository.findByActive(true).stream()
            .map(this::mapToAccountResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse deposit(Long accountId, DepositRequest request) {
        Account account = findAccountById(accountId);
        BigDecimal balanceBefore = account.getBalance();
        
        account.deposit(request.getAmount());
        accountRepository.save(account);
        
        Transaction transaction = Transaction.createDeposit(
            account,
            request.getAmount(),
            balanceBefore,
            account.getBalance()
        );
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            transaction.setDescription(request.getDescription());
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request) {
        Account account = findAccountById(accountId);
        BigDecimal balanceBefore = account.getBalance();
        
        account.withdraw(request.getAmount());
        accountRepository.save(account);
        
        Transaction transaction = Transaction.createWithdrawal(
            account,
            request.getAmount(),
            balanceBefore,
            account.getBalance()
        );
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            transaction.setDescription(request.getDescription());
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {
        Account sourceAccount = findAccountByNumber(request.getSourceAccountNumber());
        Account targetAccount = findAccountByNumber(request.getTargetAccountNumber());
        
        BigDecimal sourceBalanceBefore = sourceAccount.getBalance();
        BigDecimal targetBalanceBefore = targetAccount.getBalance();
        
        if (sourceAccount instanceof Transferable transferable) {
            transferable.transfer(targetAccount, request.getAmount());
        } else {
            throw new IllegalStateException("Source account does not support transfers");
        }
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        Transaction sourceTransaction = Transaction.createTransferOut(
            sourceAccount,
            request.getAmount(),
            sourceBalanceBefore,
            sourceAccount.getBalance(),
            targetAccount.getAccountNumber()
        );
        
        Transaction targetTransaction = Transaction.createTransferIn(
            targetAccount,
            request.getAmount(),
            targetBalanceBefore,
            targetAccount.getBalance(),
            sourceAccount.getAccountNumber()
        );
        
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            sourceTransaction.setDescription(request.getDescription() + " - Transfer to " + targetAccount.getAccountNumber());
            targetTransaction.setDescription(request.getDescription() + " - Transfer from " + sourceAccount.getAccountNumber());
        }
        
        transactionRepository.save(sourceTransaction);
        transactionRepository.save(targetTransaction);
        
        return mapToTransactionResponse(sourceTransaction);
    }

    @Override
    public AccountResponse deactivateAccount(Long accountId) {
        Account account = findAccountById(accountId);
        account.setActive(false);
        Account savedAccount = accountRepository.save(account);
        return mapToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse activateAccount(Long accountId) {
        Account account = findAccountById(accountId);
        account.setActive(true);
        Account savedAccount = accountRepository.save(account);
        return mapToAccountResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
            .map(this::mapToAccountResponse)
            .collect(Collectors.toList());
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> AccountNotFoundException.withId(id));
    }

    private Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> AccountNotFoundException.withAccountNumber(accountNumber));
    }

    private Customer findOrCreateCustomer(String fullName, String email) {
        return customerRepository.findByEmail(email).orElseGet(() -> {
            String[] parts = fullName.trim().split("\\s+", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : "";
            Customer customer = new Customer(firstName, lastName, email);
            return customerRepository.save(customer);
        });
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountHolderName(account.getAccountHolderName());
        response.setEmail(account.getEmail());
        response.setBalance(account.getBalance());
        response.setAccountType(account.getAccountType());
        response.setActive(account.isActive());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        response.setAvailableBalance(account.getBalance());

        if (account instanceof SavingsAccount savings) {
            response.setMinimumBalance(savings.getMinimumBalance());
            response.setInterestRate(savings.getInterestRate());
        } else if (account instanceof CheckingAccount checking) {
            response.setOverdraftLimit(checking.getOverdraftLimit());
            response.setMonthlyFee(checking.getMonthlyFee());
            response.setAvailableBalance(checking.getAvailableBalance());
        }

        return response;
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
