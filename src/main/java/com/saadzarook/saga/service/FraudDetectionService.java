package com.saadzarook.saga.service;

import com.saadzarook.saga.exception.FraudDetectedException;
import com.saadzarook.saga.model.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FraudDetectionService {

    public void check(TransferRequest request) throws FraudDetectedException {
        log.info("Performing fraud detection for transfer from account ID: {} to account ID: {}, amount: {}",
                request.getSenderAccountId(), request.getRecipientAccountId(), request.getAmount());

        // Implement fraud detection logic here
        boolean isFraudulent = performFraudCheck(request);

        if (isFraudulent) {
            log.warn("Fraud detected for transfer request: {}", request);
            throw new FraudDetectedException("FRAUD_CHECK_FAILURE");
        }

        log.info("No fraud detected for transfer request: {}", request);
    }

    private boolean performFraudCheck(TransferRequest request) {
        // Placeholder for actual fraud detection logic
        // For demonstration, let's assume all transactions are safe
        return false;
    }
}
