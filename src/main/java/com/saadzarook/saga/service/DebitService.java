package com.saadzarook.saga.service;

import com.saadzarook.saga.exception.AccountNotFoundException;
import com.saadzarook.saga.exception.InsufficientFundsException;
import com.saadzarook.saga.model.Account;
import com.saadzarook.saga.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitService {

    // Assume this repository interacts with the database
    private final AccountRepository accountRepository;

    public void debit(String accountId, BigDecimal amount) throws InsufficientFundsException {
        log.info("Attempting to debit account ID: {}, amount: {}", accountId, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountId);
                    return new AccountNotFoundException("Account not found: " + accountId);
                });

        if (account.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds for account ID: {}. Available balance: {}, required amount: {}",
                    accountId, account.getBalance(), amount);
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.debit(amount);
        accountRepository.save(account);
        log.info("Debited account ID: {}. New balance: {}", accountId, account.getBalance());
    }

    public void reverseDebit(String accountId, BigDecimal amount) {
        log.info("Reversing debit for account ID: {}, amount: {}", accountId, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found during reverse debit: {}", accountId);
                    return new AccountNotFoundException("Account not found: " + accountId);
                });

        account.credit(amount);
        accountRepository.save(account);
        log.info("Reversed debit for account ID: {}. New balance: {}", accountId, account.getBalance());
    }
}
