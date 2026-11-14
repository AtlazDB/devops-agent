package br.com.fatec.devopsagent.agent.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Observer: o Publisher (tambem chamado de Subject).
// Guarda a lista de inscritos e avisa todos quando algo acontece no agente.
//
// - Beans do Spring que implementam AgentObserver (ex.: LoggingObserver) entram sozinhos pelo construtor.
// - Objetos que nao sao beans (ex.: a janela Swing) se inscrevem com subscribe().
@Component
public class AgentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AgentEventPublisher.class);

    // CopyOnWriteArrayList: a GUI pode se inscrever/desinscrever enquanto o agente publica em outra thread.
    private final List<AgentObserver> observers = new CopyOnWriteArrayList<>();

    public AgentEventPublisher(List<AgentObserver> initialObservers) {
        observers.addAll(initialObservers);
    }

    public void subscribe(AgentObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(AgentObserver observer) {
        observers.remove(observer);
    }

    public void publish(AgentEvent event) {
        for (AgentObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (RuntimeException e) {
                // um observer com defeito nao pode derrubar o agente
                log.warn("observer {} falhou ao tratar {}: {}",
                        observer.getClass().getSimpleName(), event.type(), e.getMessage());
            }
        }
    }
}