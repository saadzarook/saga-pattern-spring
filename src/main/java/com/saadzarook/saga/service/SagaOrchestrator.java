package com.saadzarook.saga.service;

import com.saadzarook.saga.enums.SagaEvent;
import com.saadzarook.saga.enums.SagaState;
import com.saadzarook.saga.model.TransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {

    private final StateMachineFactory<SagaState, SagaEvent> stateMachineFactory;
    private final DebitService debitService;
    private final FraudDetectionService fraudDetectionService;
    private final CreditService creditService;
    private final NotificationService notificationService;

    public void executeSaga(TransferRequest request) {
        log.info("Starting saga execution for TransferRequest: {}", request);
        StateMachine<SagaState, SagaEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        log.debug("State machine started with initial state: {}", stateMachine.getState().getId());

        try {
            // Debit Step
            log.info("Initiating debit for account ID: {}, amount: {}", request.getSenderAccountId(), request.getAmount());
            debitService.debit(request.getSenderAccountId(), request.getAmount());
            stateMachine.sendEvent(SagaEvent.DEBIT_SUCCESS);
            log.debug("Debit successful. State transitioned to: {}", stateMachine.getState().getId());

            // Fraud Detection Step
            log.info("Performing fraud detection for transfer request.");
            fraudDetectionService.check(request);
            stateMachine.sendEvent(SagaEvent.FRAUD_CHECK_SUCCESS);
            log.debug("Fraud check passed. State transitioned to: {}", stateMachine.getState().getId());

            // Credit Step
            log.info("Initiating credit to account ID: {}, amount: {}", request.getRecipientAccountId(), request.getAmount());
            creditService.credit(request.getRecipientAccountId(), request.getAmount());
            stateMachine.sendEvent(SagaEvent.CREDIT_SUCCESS);
            log.debug("Credit successful. State transitioned to: {}", stateMachine.getState().getId());

            // Completion
            log.info("Sending confirmation notification for transfer request.");
            notificationService.sendConfirmation(request);
            stateMachine.stop();
            log.info("Saga execution completed successfully for TransferRequest: {}", request);
        } catch (Exception e) {
            log.error("Exception occurred during saga execution: {}", e.getMessage(), e);
            handleFailure(stateMachine, request, e);
        }
    }

    private void handleFailure(StateMachine<SagaState, SagaEvent> stateMachine, TransferRequest request, Exception e) {
        SagaState currentState = stateMachine.getState().getId();
        log.warn("Handling failure at state: {}. Initiating compensation.", currentState);

        // Attempt to send the failure event based on exception message
        try {
            SagaEvent failureEvent = SagaEvent.valueOf(e.getMessage());
            stateMachine.sendEvent(failureEvent);
            log.debug("Failure event sent: {}", failureEvent);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid event name in exception message: {}", e.getMessage());
            stateMachine.sendEvent(SagaEvent.FAILURE);
            log.debug("Default failure event sent: FAILURE");
        }

        // Perform compensation based on the current state
        switch (currentState) {
            case DEBIT_COMPLETED:
                log.info("Reversing debit for account ID: {}, amount: {}", request.getSenderAccountId(), request.getAmount());
                debitService.reverseDebit(request.getSenderAccountId(), request.getAmount());
                break;
            case FRAUD_CHECK_COMPLETED:
                log.info("Reversing debit due to failure after fraud check for account ID: {}, amount: {}", request.getSenderAccountId(), request.getAmount());
                debitService.reverseDebit(request.getSenderAccountId(), request.getAmount());
                break;
            case CREDIT_COMPLETED:
                log.info("Reversing credit for account ID: {}, amount: {}", request.getRecipientAccountId(), request.getAmount());
                creditService.reverseCredit(request.getRecipientAccountId(), request.getAmount());
                log.info("Reversing debit for account ID: {}, amount: {}", request.getSenderAccountId(), request.getAmount());
                debitService.reverseDebit(request.getSenderAccountId(), request.getAmount());
                break;
            default:
                log.warn("No compensation needed for current state: {}", currentState);
                break;
        }
        stateMachine.stop();
        log.info("Saga execution failed and compensations completed for TransferRequest: {}", request);
    }
}
