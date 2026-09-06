package br.com.fatec.devopsagent.agent.state;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Phase;


// Pattern State: cada fase do ciclo de vida do agente é uma classe.
// O estado faz seu trabalho e DEVOLVE o próximo estado.

public interface AgentState {

    Phase phase();

    AgentState handle(AgentContext context);

    default boolean isTerminal() {
        return false;
    }
}