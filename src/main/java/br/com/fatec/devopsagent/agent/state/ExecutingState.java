package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Phase;
import br.com.fatec.devopsagent.agent.Step;

public class ExecutingState implements AgentState {

    @Override
    public Phase phase() {
        return Phase.EXECUTING;
    }

    @Override
    public AgentState handle(AgentContext context) {
        for (Step step : context.getPlan().steps()) {

            Observation observation = context.getExecutor().execute(step, context);   // <<< único ponto de contato com o Command
            context.record(observation);

            if (!observation.success()) {
                break;
            }
        }
        return new ObservingState();
    }
}