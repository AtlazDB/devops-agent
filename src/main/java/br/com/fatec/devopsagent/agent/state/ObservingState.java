package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Phase;

public class ObservingState implements AgentState {

    @Override
    public Phase phase() {
        return Phase.OBSERVING;
    }

    @Override
    public AgentState handle(AgentContext context) {
        Observation last = context.lastObservation().orElse(null);

        if (last == null) {
            return new FailedState("nenhuma observacao produzida");
        }
        if (!last.success()) {
            return new FailedState(last.content());
        }
        if (last.action() == Action.ANSWER) {
            context.setAnswer(last.content());
            return new DoneState();
        }
        return new PlanningState();   // volta pro topo do loop
    }
}