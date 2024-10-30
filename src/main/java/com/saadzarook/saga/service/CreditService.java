package com.saadzarook.saga.service;

import com.saadzarook.saga.exception.AccountNotFoundException;
import com.saadzarook.saga.model.Account;
import com.saadzarook.saga.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    // Assume this repository interacts with the database
    private final AccountRepository accountRepository;

    public void credit(String accountId, BigDecimal amount) {
        log.info("Attempting to credit account ID: {}, amount: {}", accountId, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountId);
                    throw new AccountNotFoundException("Account not found: " + accountId);
                });

        account.credit(amount);
        accountRepository.save(account);
        log.info("Credited account ID: {}. New balance: {}", accountId, account.getBalance());
    }

    public void reverseCredit(String accountId, BigDecimal amount) {
        log.info("Reversing credit for account ID: {}, amount: {}", accountId, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found during reverse credit: {}", accountId);
                    throw new AccountNotFoundException("Account not found: " + accountId);
                });

        account.debit(amount);
        accountRepository.save(account);
        log.info("Reversed credit for account ID: {}. New balance: {}", accountId, account.getBalance());
    }
}
