package com.saadzarook.saga.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String senderAccountId;
    private String recipientAccountId;
    private BigDecimal amount;
}
