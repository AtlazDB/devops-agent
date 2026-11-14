package br.com.fatec.devopsagent.agent.observer;

import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Phase;

import java.time.Instant;

// Observer: o "aviso" que o Publisher (AgentEventPublisher) entrega para cada Subscriber (AgentObserver).
// Carrega o runId para que um observer possa filtrar so a execucao que lhe interessa.
public record AgentEvent(Type type, Long runId, Phase phase, String message, Instant at) {

    public enum Type {
        STATE_CHANGED,   // o State trocou de estado (INIT -> PLANNING -> ...)
        STEP_EXECUTED,   // um Command terminou e gerou uma Observation
        FINISHED,        // agente terminou com resposta
        FAILED           // agente terminou em FailedState
    }

    public static AgentEvent stateChanged(Long runId, Phase phase) {
        return new AgentEvent(Type.STATE_CHANGED, runId, phase, "estado -> " + phase, Instant.now());
    }

    public static AgentEvent stepExecuted(Long runId, Observation observation) {
        String status = observation.success() ? "ok" : "FALHOU";
        return new AgentEvent(Type.STEP_EXECUTED, runId, Phase.EXECUTING,
                observation.action() + " " + status, Instant.now());
    }

    public static AgentEvent finished(Long runId, String answer) {
        return new AgentEvent(Type.FINISHED, runId, Phase.DONE,
                answer == null ? "concluido (sem resposta)" : "concluido", Instant.now());
    }

    public static AgentEvent failed(Long runId, String reason) {
        return new AgentEvent(Type.FAILED, runId, Phase.FAILED, "falhou: " + reason, Instant.now());
    }
}