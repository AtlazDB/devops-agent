package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Phase;

public class InitState implements AgentState {

    @Override
    public Phase phase() {
        return Phase.INIT;
    }

    @Override
    public AgentState handle(AgentContext context) {
        if (context.getRunId() == null) {
            return new FailedState("runId nao informado");
        }
        return new PlanningState();
    }
}