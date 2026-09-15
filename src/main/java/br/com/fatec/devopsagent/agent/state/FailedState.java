package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Phase;

public class FailedState implements AgentState {

    private final String reason;

    public FailedState(String reason) {
        this.reason = reason;
    }

    public String reason() {
        return reason;
    }

    @Override
    public Phase phase() {
        return Phase.FAILED;
    }

    @Override
    public AgentState handle(AgentContext context) {
        return this;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}