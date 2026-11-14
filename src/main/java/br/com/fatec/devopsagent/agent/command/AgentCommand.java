package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.Action;
import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;

// Command: interface comum de todos os comandos.
// Cada Action do enum tem exatamente um comando concreto que sabe executa-la.
public interface AgentCommand {

    Action action();

    Observation execute(Step step, AgentContext context);
}