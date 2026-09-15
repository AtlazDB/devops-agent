package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Phase;
import br.com.fatec.devopsagent.agent.Plan;

public class PlanningState implements AgentState {

    @Override
    public Phase phase() {
        return Phase.PLANNING;
    }

    @Override
    public AgentState handle(AgentContext context) {
        if (context.exceededLimit()) {
            return new FailedState("limite de iteracoes atingido");
        }

        Plan plan = context.getStrategy().plan(context);   // único ponto de contato com a Strategy

        if (plan.isEmpty()) {
            return new DoneState();
        }
        context.setPlan(plan);
        return new ExecutingState();
    }
}