package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Phase;

public class DoneState implements AgentState {

    @Override
    public Phase phase() {
        return Phase.DONE;
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