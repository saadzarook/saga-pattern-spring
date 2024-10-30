package com.saadzarook.saga.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String accountId;
    private BigDecimal balance;

    public void credit(BigDecimal amount) {
    }

    public void debit(BigDecimal amount) {
    }
}
