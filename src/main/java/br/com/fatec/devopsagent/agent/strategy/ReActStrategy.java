package br.com.fatec.devopsagent.agent.strategy;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Plan;
import br.com.fatec.devopsagent.agent.Step;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Decide UM passo por vez, olhando o que já foi observado.
 * Mais voltas no loop, mas cada passo já nasce com o argumento certo.
 */
@Component
public class ReActStrategy implements PlanningStrategy {

    @Override
    public String name() {
        return "react";
    }

    @Override
    public Plan plan(AgentContext context) {
        return decideNextStep(context)
                .map(step -> new Plan(List.of(step)))
                .orElseGet(() -> new Plan(List.of()));
    }

    private Optional<Step> decideNextStep(AgentContext context) {

        String runId = String.valueOf(context.getRunId());

        if (!context.hasExecuted(Action.FETCH_RUN)) {
            return Optional.of(new Step(Action.FETCH_RUN, runId));
        }
        if (!context.hasExecuted(Action.FETCH_JOBS)) {
            return Optional.of(new Step(Action.FETCH_JOBS, runId));
        }
        if (!context.hasExecuted(Action.FETCH_LOGS)) {
            // A diferença aparece aqui: o jobId veio da observação anterior.
            return Optional.of(new Step(Action.FETCH_LOGS, jobIdFromObservations(context)));
        }
        if (!context.hasExecuted(Action.ANALYZE_WITH_JAN)) {
            return Optional.of(new Step(Action.ANALYZE_WITH_JAN, null));
        }
        if (!context.hasExecuted(Action.ANSWER)) {
            return Optional.of(new Step(Action.ANSWER, null));
        }
        return Optional.empty();
    }

    //Contrato com o Command: a observação de FETCH_JOBS traz o id do job que interessa (o que falhou) no campo content.

    private String jobIdFromObservations(AgentContext context) {
        return context.getObservations().stream()
                .filter(o -> o.action() == Action.FETCH_JOBS)
                .map(Observation::content)
                .findFirst()
                .orElse(null);
    }
}