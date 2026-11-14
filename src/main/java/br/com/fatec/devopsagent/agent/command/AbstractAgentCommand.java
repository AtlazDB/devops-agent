package br.com.fatec.devopsagent.agent.command;

import br.com.fatec.devopsagent.agent.AgentContext;
import br.com.fatec.devopsagent.agent.Observation;
import br.com.fatec.devopsagent.agent.Step;

// Base dos comandos: centraliza o try/catch e a criacao da Observation.
// Cada comando concreto so escreve "o que fazer" no metodo run().
// (isso e um Template Method: execute() e o esqueleto fixo, run() e o passo que muda)
public abstract class AbstractAgentCommand implements AgentCommand {

    @Override
    public final Observation execute(Step step, AgentContext context) {
        try {
            String content = run(step, context);
            return new Observation(action(), true, content);
        } catch (Exception e) {
            return new Observation(action(), false, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    protected abstract String run(Step step, AgentContext context) throws Exception;
}