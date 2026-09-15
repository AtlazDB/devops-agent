package br.com.fatec.devopsagent.agent.strategy;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Plan;
import br.com.fatec.devopsagent.agent.Step;
import org.springframework.stereotype.Component;

import java.util.List;

// Monta o plano inteiro de uma vez, antes de executar qualquer coisa.
// Rápido e previsível, mas cego: não consegue reagir ao que descobrir no caminho.

@Component
public class PlanThenExecuteStrategy implements PlanningStrategy {

    @Override
    public String name() {
        return "plan-then-execute";
    }

    @Override
    public Plan plan(AgentContext context) {

        if (!context.getObservations().isEmpty()) {
            return new Plan(List.of());   // já executou tudo numa passada só
        }

        String runId = String.valueOf(context.getRunId());

        return new Plan(List.of(
                new Step(Action.FETCH_RUN, runId),
                new Step(Action.FETCH_JOBS, runId),
                new Step(Action.FETCH_LOGS, null),        // <- não sabe o jobId ainda!
                new Step(Action.ANALYZE_WITH_JAN, null),
                new Step(Action.ANSWER, null)));
    }
}