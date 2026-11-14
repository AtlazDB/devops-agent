package br.com.fatec.devopsagent.entity;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;

import java.util.List;

// Resposta do endpoint GET /api/agent/analisar/{runId}
public record AnaliseResponse(
        Long runId,
        String strategy,
        boolean success,
        String answer,
        String error,
        List<Observation> observations
) {

    public static AnaliseResponse from(AgentContext context) {
        return new AnaliseResponse(
                context.getRunId(),
                context.getStrategy().name(),
                context.getError() == null,
                context.getAnswer(),
                context.getError(),
                context.getObservations());
    }
}