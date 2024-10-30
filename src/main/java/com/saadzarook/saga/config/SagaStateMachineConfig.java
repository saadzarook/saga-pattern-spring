package com.saadzarook.saga.config;

import com.saadzarook.saga.enums.SagaEvent;
import com.saadzarook.saga.enums.SagaState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class SagaStateMachineConfig extends StateMachineConfigurerAdapter<SagaState, SagaEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<SagaState, SagaEvent> states) throws Exception {
        states
                .withStates()
                .initial(SagaState.INITIATED)
                .state(SagaState.DEBIT_COMPLETED)
                .state(SagaState.FRAUD_CHECK_COMPLETED)
                .state(SagaState.CREDIT_COMPLETED)
                .end(SagaState.COMPLETED)
                .end(SagaState.FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SagaState, SagaEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(SagaState.INITIATED).target(SagaState.DEBIT_COMPLETED)
                .event(SagaEvent.DEBIT_SUCCESS)
                .and()
                .withExternal()
                .source(SagaState.DEBIT_COMPLETED).target(SagaState.FAILED)
                .event(SagaEvent.DEBIT_FAILURE)
                .and()
                .withExternal()
                .source(SagaState.DEBIT_COMPLETED).target(SagaState.FRAUD_CHECK_COMPLETED)
                .event(SagaEvent.FRAUD_CHECK_SUCCESS)
                .and()
                .withExternal()
                .source(SagaState.FRAUD_CHECK_COMPLETED).target(SagaState.FAILED)
                .event(SagaEvent.FRAUD_CHECK_FAILURE)
                .and()
                .withExternal()
                .source(SagaState.FRAUD_CHECK_COMPLETED).target(SagaState.CREDIT_COMPLETED)
                .event(SagaEvent.CREDIT_SUCCESS)
                .and()
                .withExternal()
                .source(SagaState.CREDIT_COMPLETED).target(SagaState.FAILED)
                .event(SagaEvent.CREDIT_FAILURE)
                .and()
                .withExternal()
                .source(SagaState.CREDIT_COMPLETED).target(SagaState.COMPLETED)
                .event(SagaEvent.CREDIT_SUCCESS);
    }
}
