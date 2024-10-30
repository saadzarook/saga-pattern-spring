package com.saadzarook.saga.service;

import com.saadzarook.saga.model.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendConfirmation(TransferRequest request) {
        log.info("Sending confirmation notification for transfer from account ID: {} to account ID: {}, amount: {}",
                request.getSenderAccountId(), request.getRecipientAccountId(), request.getAmount());

        // Implement notification logic here (e.g., send email or SMS)
        boolean notificationSent = sendNotification(request);

        if (notificationSent) {
            log.info("Confirmation notification sent successfully for transfer request: {}", request);
        } else {
            log.error("Failed to send confirmation notification for transfer request: {}", request);
            // Handle notification failure if necessary
        }
    }

    private boolean sendNotification(TransferRequest request) {
        // Placeholder for actual notification logic
        // For demonstration, let's assume the notification is always sent successfully
        return true;
    }
}
