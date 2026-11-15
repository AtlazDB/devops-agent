package br.com.fatec.devopsagent.gui.composite;

import br.com.fatec.devopsagent.agent.Action;

// Composite concreto: agrupa tudo o que aconteceu numa mesma Action (FETCH_LOGS, ANALYZE_WITH_JAN...).
public class ActionGroupNode extends CompositeNode {
    private final Action action;

    public ActionGroupNode(Action action) {
        this.action = action;
    }

    @Override
    public String label() {
        return action.name();
    }
}