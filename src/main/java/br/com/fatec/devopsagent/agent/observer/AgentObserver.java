package br.com.fatec.devopsagent.agent.observer;

// Observer: interface do Subscriber.
// Qualquer classe que queira "ouvir" o agente implementa isso e se inscreve no AgentEventPublisher.
// Implementacoes: LoggingObserver (log no console) e SwingAgentObserver (atualiza a GUI, Parte 3).
public interface AgentObserver {

    void onEvent(AgentEvent event);
}