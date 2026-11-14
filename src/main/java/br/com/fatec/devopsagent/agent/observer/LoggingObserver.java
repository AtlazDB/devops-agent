package br.com.fatec.devopsagent.agent.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Observer: Subscriber concreto que so escreve cada evento no log da aplicacao.
// Por ser @Component, o Spring o entrega ao AgentEventPublisher automaticamente.
@Component
public class LoggingObserver implements AgentObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingObserver.class);

    @Override
    public void onEvent(AgentEvent event) {
        log.info("[agent run={}] {} | {}", event.runId(), event.type(), event.message());
    }
}